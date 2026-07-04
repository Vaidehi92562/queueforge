import { useEffect, useMemo, useState } from "react";
import "./App.css";

const LANDING_IMAGE = "/landing.png";
const AUTH_IMAGE = "https://i.pinimg.com/1200x/e8/29/cd/e829cd73aa98951a5365bbaa15c1597a.jpg";

const DASHBOARD_HERO_IMAGE =
  "https://i.pinimg.com/736x/b1/ae/19/b1ae194c8f4f4f24b3a17c0af9a97932.jpg";

const DASHBOARD_ACCENT_IMAGE =
  "https://i.pinimg.com/1200x/9e/d9/08/9ed908d8cea47c8464111f63a28de1eb.jpg";

const DEFAULT_EMAIL = "vaidehi@example.com";
const DEFAULT_PASSWORD = "password123";

export default function App() {
  const savedToken = localStorage.getItem("queueforge_token");

  const [stage, setStage] = useState(savedToken ? "dashboard" : "landing");
  const [token, setToken] = useState(savedToken || "");
  const [authMode, setAuthMode] = useState("login");

  const [name, setName] = useState("Vaidehi Mishra");
  const [email, setEmail] = useState(DEFAULT_EMAIL);
  const [password, setPassword] = useState(DEFAULT_PASSWORD);

  const [metrics, setMetrics] = useState(null);
  const [workers, setWorkers] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [queues, setQueues] = useState([]);
  const [projectId, setProjectId] = useState("");
  const [selectedQueueId, setSelectedQueueId] = useState("");
  const [selectedWorkerId, setSelectedWorkerId] = useState("");

  const [activeTab, setActiveTab] = useState("atelier");
  const [loading, setLoading] = useState(false);
  const [actionBusy, setActionBusy] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (stage === "landing" && !token) {
      const timer = setTimeout(() => setStage("auth"), 10000);
      return () => clearTimeout(timer);
    }
  }, [stage, token]);

  async function readJsonSafely(response) {
    const text = await response.text();

    if (!text) {
      if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
      }
      return {};
    }

    try {
      return JSON.parse(text);
    } catch {
      throw new Error(text || "Server returned invalid response");
    }
  }

  async function api(path, options = {}) {
    const response = await fetch(path, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.headers || {})
      }
    });

    const data = await readJsonSafely(response);

    if (!response.ok || data.success === false) {
      throw new Error(data.message || "Request failed");
    }

    return data.data ?? data;
  }

  async function login(event) {
    event.preventDefault();
    setLoading(true);
    setMessage("");

    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      const data = await readJsonSafely(response);

      if (!response.ok || data.success === false) {
        throw new Error(data.message || "Login failed");
      }

      localStorage.setItem("queueforge_token", data.data.token);
      setToken(data.data.token);
      setStage("dashboard");
      setMessage("Welcome back. QueueForge atelier is live.");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function register(event) {
    event.preventDefault();
    setLoading(true);
    setMessage("");

    try {
      const response = await fetch("/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, password })
      });

      const data = await readJsonSafely(response);

      if (!response.ok || data.success === false) {
        throw new Error(data.message || "Registration failed");
      }

      setAuthMode("login");
      setMessage("Account created with default workspace. Login now.");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function loadDashboard() {
    if (!token) return;

    setLoading(true);

    try {
      const metricData = await api("/api/metrics/dashboard");
      setMetrics(metricData);

      const workerData = await api("/api/workers");
      setWorkers(workerData || []);
      if (!selectedWorkerId && workerData?.length) {
        setSelectedWorkerId(workerData[0].id);
      }

      const orgs = await api("/api/organizations");

      if (orgs && orgs.length > 0) {
        const projects = await api(`/api/projects?organizationId=${orgs[0].id}`);

        if (projects && projects.length > 0) {
          const currentProjectId = projects[0].id;
          setProjectId(currentProjectId);

          const queueData = await api(`/api/queues?projectId=${currentProjectId}`);
          setQueues(queueData || []);

          if (!selectedQueueId && queueData?.length) {
            setSelectedQueueId(queueData[0].id);
          }

          const jobData = await api(`/api/jobs?projectId=${currentProjectId}`);
          const sortedJobs = (jobData || [])
            .slice()
            .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0));

          setJobs(sortedJobs);
        }
      }
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  function logout() {
    localStorage.removeItem("queueforge_token");
    setToken("");
    setStage("landing");
    setMetrics(null);
    setWorkers([]);
    setJobs([]);
    setQueues([]);
    setProjectId("");
    setSelectedQueueId("");
    setSelectedWorkerId("");
    setMessage("");
  }

  useEffect(() => {
    if (!token || stage !== "dashboard") return;

    loadDashboard();
    const intervalId = setInterval(loadDashboard, 7000);
    return () => clearInterval(intervalId);
  }, [token, stage]);

  async function runAction(successMessage, action) {
    setActionBusy(true);
    setMessage("");

    try {
      await action();
      await loadDashboard();
      setMessage(successMessage);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setActionBusy(false);
    }
  }

  function getSelectedQueue() {
    const queue = queues.find((item) => item.id === selectedQueueId) || queues[0];
    if (!queue) {
      throw new Error("No queue found. Register/login again or create default queue first.");
    }
    return queue;
  }

  async function getOrCreateWorker() {
    /*
      For demo actions, always create a fresh worker.
      This avoids stale/offline/draining workers causing:
      "Worker is not available for claiming jobs".
    */
    const worker = await api("/api/workers/register", {
      method: "POST",
      body: JSON.stringify({
        name: `Demo Worker ${new Date().toLocaleTimeString()}`,
        maxConcurrency: 5
      })
    });

    await api(`/api/workers/${worker.id}/heartbeat`, {
      method: "POST",
      body: JSON.stringify({ currentLoad: 0 })
    });

    setSelectedWorkerId(worker.id);
    return worker;
  }

  function createJobPayload(kind) {
    return {
      kind,
      source: "QueueForge Dashboard",
      createdAt: new Date().toISOString(),
      demo: true
    };
  }

  async function createImmediateJob() {
    const queue = getSelectedQueue();

    await api("/api/jobs/immediate", {
      method: "POST",
      body: JSON.stringify({
        queueId: queue.id,
        type: "IMMEDIATE",
        payload: createJobPayload("immediate-job"),
        priority: 8,
        idempotencyKey: `immediate-${Date.now()}`
      })
    });
  }

  async function createDelayedJob() {
    const queue = getSelectedQueue();

    await api("/api/jobs/delayed", {
      method: "POST",
      body: JSON.stringify({
        queueId: queue.id,
        type: "DELAYED",
        payload: createJobPayload("delayed-job"),
        priority: 6,
        runAt: new Date(Date.now() + 2 * 60 * 1000).toISOString(),
        idempotencyKey: `delayed-${Date.now()}`
      })
    });
  }

  async function createScheduledJob() {
    const queue = getSelectedQueue();

    await api("/api/jobs/scheduled", {
      method: "POST",
      body: JSON.stringify({
        queueId: queue.id,
        type: "SCHEDULED",
        payload: createJobPayload("scheduled-job"),
        priority: 7,
        runAt: new Date(Date.now() + 5 * 60 * 1000).toISOString(),
        idempotencyKey: `scheduled-${Date.now()}`
      })
    });
  }

  async function createRecurringJob() {
    const queue = getSelectedQueue();

    await api("/api/jobs/recurring", {
      method: "POST",
      body: JSON.stringify({
        queueId: queue.id,
        name: `Recurring Health Pulse ${Date.now()}`,
        cronExpression: "0 */1 * * * *",
        payloadTemplate: createJobPayload("recurring-health-pulse"),
        nextRunAt: new Date(Date.now() + 60 * 1000).toISOString()
      })
    });
  }

  async function createBatchJobs() {
    const queue = getSelectedQueue();

    await api("/api/jobs/batch", {
      method: "POST",
      body: JSON.stringify({
        jobs: Array.from({ length: 5 }).map((_, index) => ({
          queueId: queue.id,
          type: "IMMEDIATE",
          payload: {
            ...createJobPayload("batch-job"),
            batchIndex: index + 1
          },
          priority: 5 + index,
          idempotencyKey: `batch-${Date.now()}-${index}`
        }))
      })
    });
  }

  async function registerDemoWorker() {
    const worker = await api("/api/workers/register", {
      method: "POST",
      body: JSON.stringify({
        name: `Demo Worker ${new Date().toLocaleTimeString()}`,
        maxConcurrency: 5
      })
    });

    setSelectedWorkerId(worker.id);
  }

  async function claimJob() {
    const worker = await getOrCreateWorker();

    await api(`/api/workers/${worker.id}/claim`, {
      method: "POST",
      body: JSON.stringify({ maxJobs: 1 })
    });
  }

  async function heartbeatWorker() {
    const worker = await getOrCreateWorker();

    await api(`/api/workers/${worker.id}/heartbeat`, {
      method: "POST",
      body: JSON.stringify({ currentLoad: Math.floor(Math.random() * 3) })
    });
  }

  async function oneClickCompleteFlow() {
    const queue = getSelectedQueue();
    const worker = await getOrCreateWorker();

    const job = await api("/api/jobs/immediate", {
      method: "POST",
      body: JSON.stringify({
        queueId: queue.id,
        type: "IMMEDIATE",
        payload: createJobPayload("one-click-complete-flow"),
        priority: 100,
        idempotencyKey: `complete-flow-${Date.now()}`
      })
    });

    const claimResponse = await api(`/api/workers/${worker.id}/claim`, {
      method: "POST",
      body: JSON.stringify({ maxJobs: 1 })
    });

    const claimedJob =
      claimResponse.jobs?.[0] ||
      claimResponse.claimedJobs?.[0] ||
      claimResponse.claimedJobResponses?.[0] ||
      job;

    const jobId = claimedJob.id || job.id;

    await api(`/api/workers/${worker.id}/jobs/${jobId}/start`, {
      method: "POST"
    });

    await api(`/api/workers/${worker.id}/jobs/${jobId}/complete`, {
      method: "POST",
      body: JSON.stringify({
        resultMessage: "Completed from QueueForge dashboard demo flow."
      })
    });
  }

  async function oneClickFailFlow() {
    const queue = getSelectedQueue();
    const worker = await getOrCreateWorker();

    const job = await api("/api/jobs/immediate", {
      method: "POST",
      body: JSON.stringify({
        queueId: queue.id,
        type: "IMMEDIATE",
        payload: createJobPayload("one-click-fail-retry-flow"),
        priority: 99,
        idempotencyKey: `fail-flow-${Date.now()}`
      })
    });

    const claimResponse = await api(`/api/workers/${worker.id}/claim`, {
      method: "POST",
      body: JSON.stringify({ maxJobs: 1 })
    });

    const claimedJob =
      claimResponse.jobs?.[0] ||
      claimResponse.claimedJobs?.[0] ||
      claimResponse.claimedJobResponses?.[0] ||
      job;

    const jobId = claimedJob.id || job.id;

    await api(`/api/workers/${worker.id}/jobs/${jobId}/start`, {
      method: "POST"
    });

    await api(`/api/workers/${worker.id}/jobs/${jobId}/fail`, {
      method: "POST",
      body: JSON.stringify({
        errorMessage: "Simulated dashboard failure to demonstrate retry/DLQ path."
      })
    });
  }

  async function pauseSelectedQueue() {
    const queue = getSelectedQueue();

    await api(`/api/queues/${queue.id}/pause`, {
      method: "PATCH"
    });
  }

  async function resumeSelectedQueue() {
    const queue = getSelectedQueue();

    await api(`/api/queues/${queue.id}/resume`, {
      method: "PATCH"
    });
  }

  async function startJob(job) {
    const worker = await getOrCreateWorker();

    await api(`/api/workers/${worker.id}/jobs/${job.id}/start`, {
      method: "POST"
    });
  }

  async function completeJob(job) {
    const worker = await getOrCreateWorker();

    await api(`/api/workers/${worker.id}/jobs/${job.id}/complete`, {
      method: "POST",
      body: JSON.stringify({
        resultMessage: "Completed from job gallery."
      })
    });
  }

  async function failJob(job) {
    const worker = await getOrCreateWorker();

    await api(`/api/workers/${worker.id}/jobs/${job.id}/fail`, {
      method: "POST",
      body: JSON.stringify({
        errorMessage: "Failed from job gallery demo action."
      })
    });
  }

  const latestJobs = useMemo(() => jobs.slice(0, 8), [jobs]);
  const dlqJobs = useMemo(() => jobs.filter((job) => job.status === "DEAD_LETTER"), [jobs]);
  const retryingJobs = useMemo(() => jobs.filter((job) => job.status === "RETRYING"), [jobs]);

  const actions = {
    createImmediateJob: () => runAction("Immediate job created.", createImmediateJob),
    createDelayedJob: () => runAction("Delayed job created for two minutes later.", createDelayedJob),
    createScheduledJob: () => runAction("Scheduled job created for five minutes later.", createScheduledJob),
    createRecurringJob: () => runAction("Recurring job created. Scheduler will generate runs.", createRecurringJob),
    createBatchJobs: () => runAction("Batch of five jobs created.", createBatchJobs),
    registerDemoWorker: () => runAction("Demo worker registered.", registerDemoWorker),
    claimJob: () => runAction("Worker claim action completed.", claimJob),
    heartbeatWorker: () => runAction("Worker heartbeat updated.", heartbeatWorker),
    oneClickCompleteFlow: () => runAction("Full flow completed: create → claim → start → complete.", oneClickCompleteFlow),
    oneClickFailFlow: () => runAction("Failure flow executed: create → claim → start → fail/retry.", oneClickFailFlow),
    pauseSelectedQueue: () => runAction("Queue paused.", pauseSelectedQueue),
    resumeSelectedQueue: () => runAction("Queue resumed.", resumeSelectedQueue),
    startJob: (job) => runAction("Job started.", () => startJob(job)),
    completeJob: (job) => runAction("Job completed.", () => completeJob(job)),
    failJob: (job) => runAction("Job failed and retry/DLQ handler executed.", () => failJob(job))
  };

  if (stage === "landing") {
    return <LandingScreen onEnter={() => setStage("auth")} />;
  }

  if (stage === "auth") {
    return (
      <AuthScreen
        authMode={authMode}
        setAuthMode={setAuthMode}
        name={name}
        setName={setName}
        email={email}
        setEmail={setEmail}
        password={password}
        setPassword={setPassword}
        login={login}
        register={register}
        loading={loading}
        message={message}
      />
    );
  }

  return (
    <Dashboard
      metrics={metrics}
      workers={workers}
      jobs={jobs}
      latestJobs={latestJobs}
      retryingJobs={retryingJobs}
      dlqJobs={dlqJobs}
      queues={queues}
      projectId={projectId}
      selectedQueueId={selectedQueueId}
      setSelectedQueueId={setSelectedQueueId}
      selectedWorkerId={selectedWorkerId}
      setSelectedWorkerId={setSelectedWorkerId}
      activeTab={activeTab}
      setActiveTab={setActiveTab}
      loading={loading}
      actionBusy={actionBusy}
      message={message}
      refresh={loadDashboard}
      logout={logout}
      actions={actions}
    />
  );
}

function LandingScreen({ onEnter }) {
  return (
    <main className="split-stage">
      <section className="visual-panel landing-visual">
        <img src={LANDING_IMAGE} alt="QueueForge landing" />
      </section>

      <section className="copy-panel">
        <div className="pastel-blob blob-one" />
        <div className="pastel-blob blob-two" />

        <article className="landing-card">
          <p className="kicker">Pastel Operations Studio</p>
          <h1>
            QForge
            <span>Control Center</span>
          </h1>
          <p className="large-line">A calm command room for jobs that never stop moving.</p>
          <p className="soft-copy">
            Schedule every flow, claim without conflict, recover without panic, and watch your
            distributed system breathe in real time.
          </p>

          <div className="landing-tags">
            <span>Atomic Claiming</span>
            <span>Retry Recovery</span>
            <span>Cron Intelligence</span>
          </div>

          <button className="primary-btn" onClick={onEnter}>Enter Early</button>

          <div className="timer-track">
            <div />
          </div>
          <small>Auto-opening login in 10 seconds</small>
        </article>
      </section>
    </main>
  );
}

function AuthScreen({
  authMode,
  setAuthMode,
  name,
  setName,
  email,
  setEmail,
  password,
  setPassword,
  login,
  register,
  loading,
  message
}) {
  return (
    <main className="split-stage">
      <section className="visual-panel auth-visual">
        <img src={AUTH_IMAGE} alt="QueueForge login" />
      </section>

      <section className="copy-panel auth-copy">
        <div className="pastel-blob blob-three" />
        <div className="pastel-blob blob-four" />

        <article className="auth-card">
          <p className="kicker">Operator Gateway</p>
          <h2>{authMode === "login" ? "Enter the flow room." : "Create your command key."}</h2>
          <p className="soft-copy">
            Manage queues, workers, retries, recurring jobs and recovery from one elegant pastel desk.
          </p>

          <div className="mode-switch">
            <button className={authMode === "login" ? "active" : ""} onClick={() => setAuthMode("login")}>
              Login
            </button>
            <button className={authMode === "register" ? "active" : ""} onClick={() => setAuthMode("register")}>
              Register
            </button>
          </div>

          <form onSubmit={authMode === "login" ? login : register} className="form-stack">
            {authMode === "register" && (
              <>
                <label>Name</label>
                <input value={name} onChange={(event) => setName(event.target.value)} />
              </>
            )}

            <label>Email</label>
            <input value={email} onChange={(event) => setEmail(event.target.value)} />

            <label>Password</label>
            <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} />

            <button className="primary-btn" type="submit" disabled={loading}>
              {loading ? "Please wait..." : authMode === "login" ? "Open Control Center" : "Create Account"}
            </button>
          </form>

          {message && <p className="message">{message}</p>}
        </article>
      </section>
    </main>
  );
}

function Dashboard({
  metrics,
  workers,
  jobs,
  latestJobs,
  retryingJobs,
  dlqJobs,
  queues,
  projectId,
  selectedQueueId,
  setSelectedQueueId,
  selectedWorkerId,
  setSelectedWorkerId,
  activeTab,
  setActiveTab,
  loading,
  actionBusy,
  message,
  refresh,
  logout,
  actions
}) {
  const totalJobs = metrics?.totalJobs ?? jobs.length;
  const queued = metrics?.queuedJobs ?? jobs.filter((job) => job.status === "QUEUED").length;
  const running = metrics?.runningJobs ?? jobs.filter((job) => job.status === "RUNNING").length;
  const completed = metrics?.completedJobs ?? jobs.filter((job) => job.status === "COMPLETED").length;
  const retrying = metrics?.retryingJobs ?? retryingJobs.length;
  const deadLetter = metrics?.deadLetterJobs ?? dlqJobs.length;
  const totalWorkers = metrics?.totalWorkers ?? workers.length;
  const totalQueues = metrics?.totalQueues ?? queues.length;

  const queuedPct = percentage(queued, totalJobs);
  const completedPct = percentage(completed, totalJobs);
  const retryPct = percentage(retrying, totalJobs);

  const cards = [
    ["Total Jobs", totalJobs, "sage"],
    ["Queued", queued, "mint"],
    ["Running", running, "sky"],
    ["Completed", completed, "cream"],
    ["Retrying", retrying, "honey"],
    ["Dead Letter", deadLetter, "rose"],
    ["Workers", totalWorkers, "blue"],
    ["Queues", totalQueues, "peach"]
  ];

  return (
    <main className="atelier-shell">
      <div className="atelier-bg bg-a" />
      <div className="atelier-bg bg-b" />
      <div className="atelier-bg bg-c" />

      <aside className="atelier-sidebar">
        <div className="brand-lockup">
          <div className="brand-orb">Q</div>
          <div>
            <h3>QueueForge</h3>
            <p>Pulse Atelier</p>
          </div>
        </div>

        <nav>
          {[
            ["atelier", "Atelier"],
            ["actions", "Action Center"],
            ["jobs", "Job Gallery"],
            ["workers", "Worker Garden"],
            ["queues", "Queue Rooms"],
            ["dlq", "Recovery Shelf"]
          ].map(([key, label]) => (
            <button
              key={key}
              className={activeTab === key ? "selected" : ""}
              onClick={() => setActiveTab(key)}
            >
              {label}
            </button>
          ))}
        </nav>

        <div className="side-note">
          <span>Active Project</span>
          <strong>{projectId ? shortId(projectId) : "Loading..."}</strong>
          <button onClick={logout}>Logout</button>
        </div>
      </aside>

      <section className="atelier-main">
        <header className="atelier-hero">
          <div className="hero-text">
            <p className="kicker">Live control atelier</p>
            <h1>{titleForTab(activeTab)}</h1>
            <p>
              A softer command deck for a hard-working distributed scheduler — visible,
              elegant, and quietly powerful.
            </p>
          </div>

          <div className="hero-actions">
            <div className="live-pill">
              <i />
              System online
            </div>
            <button className="primary-btn" onClick={refresh} disabled={loading}>
              {loading ? "Syncing..." : "Refresh"}
            </button>
          </div>
        </header>

        {message && <div className="toast">{message}</div>}

        {(activeTab === "atelier" || activeTab === "actions") && (
          <ActionCenter
            queues={queues}
            workers={workers}
            selectedQueueId={selectedQueueId}
            setSelectedQueueId={setSelectedQueueId}
            selectedWorkerId={selectedWorkerId}
            setSelectedWorkerId={setSelectedWorkerId}
            actions={actions}
            actionBusy={actionBusy}
          />
        )}

        {activeTab === "atelier" && (
          <>
            <section className="hero-bento">
              <article className="bento-copy glass-card">
                <p className="kicker">QueueForge control room</p>
                <h2>Where every queue feels curated, not chaotic.</h2>
                <p>
                  Schedule, claim, execute, recover and monitor everything from one pastel
                  operations desk.
                </p>

                <div className="flow-strip">
                  <span>Schedule</span>
                  <i />
                  <span>Claim</span>
                  <i />
                  <span>Execute</span>
                  <i />
                  <span>Recover</span>
                </div>

                <div className="mini-stat-row">
                  <MiniStat label="Total workload" value={totalJobs} />
                  <MiniStat label="Workers active" value={totalWorkers} />
                  <MiniStat label="Queues managed" value={totalQueues} />
                </div>
              </article>

              <article className="bento-image glass-card">
                <img src={DASHBOARD_HERO_IMAGE} alt="Dashboard visual" />
                <div className="image-float-card">
                  <span>Control mood</span>
                  <strong>Pastel precision</strong>
                  <p>Elegant orchestration with soft visual hierarchy.</p>
                </div>
              </article>
            </section>

            <section className="metrics-grid">
              {cards.map(([label, value, tone]) => (
                <MetricCard key={label} label={label} value={value} tone={tone} />
              ))}
            </section>

            <section className="dashboard-grid">
              <article className="glass-card progress-card">
                <p className="kicker">Operational canvas</p>
                <h2>Flow quality snapshot</h2>

                <Progress label="Queued load" value={queuedPct} />
                <Progress label="Completion rate" value={completedPct} />
                <Progress label="Recovery pressure" value={retryPct} />
              </article>

              <article className="story-card">
                <img src={DASHBOARD_ACCENT_IMAGE} alt="Pastel dashboard story" />
                <div>
                  <p className="kicker">Visual command lane</p>
                  <h2>Soft visuals, sharp operations.</h2>
                  <p>
                    A dashboard that feels editorial and premium — not just another admin panel.
                  </p>
                </div>
              </article>
            </section>

            <section className="dashboard-grid">
              <Panel title="Recent Job Gallery">
                <JobsTable jobs={latestJobs} compact actions={actions} />
              </Panel>

              <Panel title="Worker Pulse">
                <div className="worker-list">
                  {workers.slice(0, 5).map((worker) => (
                    <WorkerCard key={worker.id} worker={worker} />
                  ))}
                </div>
              </Panel>
            </section>
          </>
        )}

        {activeTab === "actions" && (
          <section className="dashboard-grid">
            <Panel title="Action Result Preview">
              <JobsTable jobs={latestJobs} actions={actions} />
            </Panel>

            <Panel title="Live Worker Fleet">
              <div className="worker-list">
                {workers.slice(0, 5).map((worker) => (
                  <WorkerCard key={worker.id} worker={worker} />
                ))}
              </div>
            </Panel>
          </section>
        )}

        {activeTab === "jobs" && (
          <Panel title="Job Gallery">
            <JobsTable jobs={jobs} actions={actions} />
          </Panel>
        )}

        {activeTab === "workers" && (
          <Panel title="Worker Garden">
            <div className="worker-grid">
              {workers.map((worker) => (
                <WorkerCard key={worker.id} worker={worker} />
              ))}
            </div>
          </Panel>
        )}

        {activeTab === "queues" && (
          <Panel title="Queue Rooms">
            <div className="queue-grid">
              {queues.map((queue) => (
                <article className="queue-card" key={queue.id}>
                  <div>
                    <h3>{queue.name}</h3>
                    <p>{shortId(queue.id)}</p>
                  </div>
                  <span className={queue.paused ? "queue-pill paused" : "queue-pill"}>
                    {queue.paused ? "Paused" : "Active"}
                  </span>
                  <div className="queue-meta">
                    <span>Priority {queue.priority}</span>
                    <span>Concurrency {queue.maxConcurrency}</span>
                    <span>{queue.rateLimitPerMinute}/min</span>
                  </div>
                </article>
              ))}
            </div>
          </Panel>
        )}

        {activeTab === "dlq" && (
          <Panel title="Recovery Shelf">
            {dlqJobs.length ? (
              <JobsTable jobs={dlqJobs} actions={actions} />
            ) : (
              <Empty text="No dead-letter jobs. Recovery shelf is clean." />
            )}
          </Panel>
        )}
      </section>
    </main>
  );
}

function ActionCenter({
  queues,
  workers,
  selectedQueueId,
  setSelectedQueueId,
  selectedWorkerId,
  setSelectedWorkerId,
  actions,
  actionBusy
}) {
  return (
    <section className="action-center glass-card">
      <div className="action-head">
        <div>
          <p className="kicker">Action Center</p>
          <h2>Operate the scheduler from the UI.</h2>
          <p>
            Create jobs, register workers, claim workload, complete flows and test recovery paths.
          </p>
        </div>

        <div className="selector-stack">
          <label>Queue</label>
          <select value={selectedQueueId} onChange={(event) => setSelectedQueueId(event.target.value)}>
            {queues.map((queue) => (
              <option key={queue.id} value={queue.id}>
                {queue.name} {queue.paused ? "(paused)" : "(active)"}
              </option>
            ))}
          </select>

          <label>Worker</label>
          <select value={selectedWorkerId} onChange={(event) => setSelectedWorkerId(event.target.value)}>
            <option value="">Auto-create / first worker</option>
            {workers.map((worker) => (
              <option key={worker.id} value={worker.id}>
                {worker.name} — {worker.status}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="action-grid">
        <ActionButton title="Immediate Job" copy="Create a job that is instantly queueable." onClick={actions.createImmediateJob} disabled={actionBusy} />
        <ActionButton title="Delayed Job" copy="Create a job scheduled two minutes ahead." onClick={actions.createDelayedJob} disabled={actionBusy} />
        <ActionButton title="Scheduled Job" copy="Create a precise future job five minutes ahead." onClick={actions.createScheduledJob} disabled={actionBusy} />
        <ActionButton title="Recurring Job" copy="Create a cron pulse that generates jobs." onClick={actions.createRecurringJob} disabled={actionBusy} />
        <ActionButton title="Batch Jobs" copy="Create five demo jobs in one API call." onClick={actions.createBatchJobs} disabled={actionBusy} />
        <ActionButton title="Register Worker" copy="Add a new worker node to the fleet." onClick={actions.registerDemoWorker} disabled={actionBusy} />
        <ActionButton title="Claim Job" copy="Let a worker atomically claim work." onClick={actions.claimJob} disabled={actionBusy} />
        <ActionButton title="Heartbeat" copy="Update worker load and heartbeat." onClick={actions.heartbeatWorker} disabled={actionBusy} />
        <ActionButton title="Complete Flow" copy="Create → claim → start → complete." onClick={actions.oneClickCompleteFlow} disabled={actionBusy} strong />
        <ActionButton title="Fail / Retry Flow" copy="Create → claim → start → fail." onClick={actions.oneClickFailFlow} disabled={actionBusy} danger />
        <ActionButton title="Pause Queue" copy="Stop queue from accepting execution." onClick={actions.pauseSelectedQueue} disabled={actionBusy} />
        <ActionButton title="Resume Queue" copy="Bring selected queue back online." onClick={actions.resumeSelectedQueue} disabled={actionBusy} />
      </div>
    </section>
  );
}

function ActionButton({ title, copy, onClick, disabled, strong, danger }) {
  return (
    <button
      className={`action-button ${strong ? "strong" : ""} ${danger ? "danger" : ""}`}
      onClick={onClick}
      disabled={disabled}
    >
      <strong>{title}</strong>
      <span>{copy}</span>
    </button>
  );
}

function MetricCard({ label, value, tone }) {
  return (
    <article className={`metric-card ${tone}`}>
      <span>{label}</span>
      <strong>{formatNumber(value)}</strong>
    </article>
  );
}

function MiniStat({ label, value }) {
  return (
    <div className="mini-stat">
      <strong>{formatNumber(value)}</strong>
      <span>{label}</span>
    </div>
  );
}

function Progress({ label, value }) {
  return (
    <div className="progress-item">
      <div>
        <span>{label}</span>
        <strong>{value}%</strong>
      </div>
      <div className="progress-track">
        <i style={{ width: `${value}%` }} />
      </div>
    </div>
  );
}

function Panel({ title, children }) {
  return (
    <section className="panel glass-card">
      <h2>{title}</h2>
      {children}
    </section>
  );
}

function JobsTable({ jobs, compact = false, actions }) {
  if (!jobs.length) return <Empty text="No jobs available yet. Use Action Center to create jobs." />;

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {!compact && <th>Job</th>}
            <th>Type</th>
            <th>Status</th>
            <th>Priority</th>
            <th>Attempt</th>
            {!compact && <th>Run At</th>}
            <th>Created</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {jobs.map((job) => (
            <tr key={job.id}>
              {!compact && <td className="mono">{shortId(job.id)}</td>}
              <td>{job.type}</td>
              <td><Badge value={job.status} /></td>
              <td>{job.priority}</td>
              <td>{job.currentAttempt}/{job.maxAttempts}</td>
              {!compact && <td>{job.runAt ? formatDate(job.runAt) : "Immediate"}</td>}
              <td>{formatDate(job.createdAt)}</td>
              <td>
                <div className="row-actions">
                  {job.status === "CLAIMED" && (
                    <button onClick={() => actions.startJob(job)}>Start</button>
                  )}
                  {job.status === "RUNNING" && (
                    <>
                      <button onClick={() => actions.completeJob(job)}>Complete</button>
                      <button className="danger" onClick={() => actions.failJob(job)}>Fail</button>
                    </>
                  )}
                  {job.status !== "CLAIMED" && job.status !== "RUNNING" && (
                    <span className="muted-action">Auto</span>
                  )}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function WorkerCard({ worker }) {
  const loadPercent = worker.maxConcurrency
    ? Math.round((worker.currentLoad / worker.maxConcurrency) * 100)
    : 0;

  return (
    <article className="worker-card">
      <div className="worker-top">
        <div>
          <h3>{worker.name}</h3>
          <p>{shortId(worker.id)}</p>
        </div>
        <Badge value={worker.status} />
      </div>

      <div className="load-row">
        <span>Load {worker.currentLoad}/{worker.maxConcurrency}</span>
        <strong>{loadPercent}%</strong>
      </div>

      <div className="progress-track">
        <i style={{ width: `${Math.min(loadPercent, 100)}%` }} />
      </div>

      <small>
        Heartbeat: {worker.lastHeartbeatAt ? formatDate(worker.lastHeartbeatAt) : "Not available"}
      </small>
    </article>
  );
}

function Badge({ value }) {
  return <span className={`badge ${String(value).toLowerCase()}`}>{value}</span>;
}

function Empty({ text }) {
  return <div className="empty-state">{text}</div>;
}

function titleForTab(tab) {
  return {
    atelier: "QueueForge Pulse Atelier",
    actions: "Action Center",
    jobs: "Job Gallery",
    workers: "Worker Garden",
    queues: "Queue Rooms",
    dlq: "Recovery Shelf"
  }[tab];
}

function percentage(part, total) {
  if (!total || total <= 0) return 0;
  return Math.min(100, Math.round((part / total) * 100));
}

function shortId(id) {
  if (!id) return "-";
  return `${id.slice(0, 8)}...${id.slice(-4)}`;
}

function formatDate(value) {
  if (!value) return "-";
  return new Date(value).toLocaleString();
}

function formatNumber(value) {
  return new Intl.NumberFormat("en-IN").format(value || 0);
}
