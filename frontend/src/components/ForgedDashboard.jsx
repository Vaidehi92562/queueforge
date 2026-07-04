import "./ForgedDashboard.css";

const HERO_IMAGE =
  "https://i.pinimg.com/736x/b1/ae/19/b1ae194c8f4f4f24b3a17c0af9a97932.jpg";

const ACCENT_IMAGE =
  "https://i.pinimg.com/1200x/9e/d9/08/9ed908d8cea47c8464111f63a28de1eb.jpg";

function pickNumber(source, candidates, fallback = 0) {
  for (const key of candidates) {
    const value = key.split(".").reduce((acc, part) => acc?.[part], source);
    if (typeof value === "number" && !Number.isNaN(value)) return value;
  }
  return fallback;
}

function formatCount(value) {
  return new Intl.NumberFormat("en-IN").format(value ?? 0);
}

function percent(part, total) {
  if (!total || total <= 0) return 0;
  return Math.min(100, Math.round((part / total) * 100));
}

export default function ForgedDashboard({ dashboardData, onLogout }) {
  const totalJobs = pickNumber(dashboardData, [
    "totalJobs",
    "jobs.total",
    "metrics.totalJobs",
    "data.totalJobs",
  ]);

  const queued = pickNumber(dashboardData, [
    "queuedJobs",
    "queued",
    "jobs.queued",
    "metrics.queued",
    "jobStatus.queued",
    "data.queued",
  ]);

  const running = pickNumber(dashboardData, [
    "runningJobs",
    "running",
    "jobs.running",
    "metrics.running",
    "jobStatus.running",
    "data.running",
  ]);

  const completed = pickNumber(dashboardData, [
    "completedJobs",
    "completed",
    "jobs.completed",
    "metrics.completed",
    "jobStatus.completed",
    "data.completed",
  ]);

  const retrying = pickNumber(dashboardData, [
    "retryingJobs",
    "retrying",
    "jobs.retrying",
    "metrics.retrying",
    "jobStatus.retrying",
    "data.retrying",
  ]);

  const deadLetters = pickNumber(dashboardData, [
    "deadLetterJobs",
    "deadLetters",
    "dlq",
    "jobs.deadLetters",
    "metrics.deadLetters",
    "jobStatus.deadLetter",
    "data.deadLetters",
  ]);

  const workers = pickNumber(dashboardData, [
    "workers",
    "workerCount",
    "metrics.workers",
    "data.workers",
  ]);

  const queues = pickNumber(dashboardData, [
    "queues",
    "queueCount",
    "metrics.queues",
    "data.queues",
  ]);

  const completedPct = percent(completed, totalJobs);
  const queuedPct = percent(queued, totalJobs);
  const retryPct = percent(retrying, totalJobs);
  const workerLoad = queues > 0 ? Math.round((workers / queues) * 100) / 100 : workers;

  const metricCards = [
    { label: "Total Jobs", value: totalJobs, tone: "emerald" },
    { label: "Queued", value: queued, tone: "mint" },
    { label: "Running", value: running, tone: "sky" },
    { label: "Completed", value: completed, tone: "amber" },
    { label: "Retrying", value: retrying, tone: "rose" },
    { label: "Dead Letter", value: deadLetters, tone: "peach" },
    { label: "Workers", value: workers, tone: "sage" },
    { label: "Queues", value: queues, tone: "lilac" },
  ];

  const controlNotes = [
    {
      title: "Queue Rhythm",
      value: `${queuedPct}% queued`,
      copy: "Backlog visibility with a calm, readable signal.",
    },
    {
      title: "Recovery Watch",
      value: `${retryPct}% retry path`,
      copy: "Retry + DLQ health kept in one operational lane.",
    },
    {
      title: "Finish Rate",
      value: `${completedPct}% completed`,
      copy: "Delivery momentum surfaced in a clean pastel card.",
    },
    {
      title: "Fleet Balance",
      value: `${workerLoad} workers/queue`,
      copy: "Worker-to-queue density at a glance.",
    },
  ];

  return (
    <div className="forged-shell">
      <div className="forged-bg forged-bg-a" />
      <div className="forged-bg forged-bg-b" />
      <div className="forged-bg forged-bg-c" />

      <header className="forged-topbar">
        <div>
          <span className="forged-chip">Live control atelier</span>
          <h1>QueueForge Pulse Atelier</h1>
          <p>
            A softer command deck for a hard-working distributed scheduler —
            visible, elegant, and quietly powerful.
          </p>
        </div>

        <div className="forged-top-actions">
          <div className="forged-pill">
            <span className="dot live" />
            System online
          </div>
          <button className="forged-logout" onClick={onLogout}>
            Logout
          </button>
        </div>
      </header>

      <section className="hero-grid">
        <div className="hero-copy card glass">
          <span className="eyebrow">QueueForge control room</span>
          <h2>Where every queue feels curated, not chaotic.</h2>
          <p>
            Schedule, claim, execute, recover and monitor everything from one
            pastel operations desk.
          </p>

          <div className="hero-flow">
            <div>Schedule</div>
            <span>→</span>
            <div>Claim</div>
            <span>→</span>
            <div>Execute</div>
            <span>→</span>
            <div>Recover</div>
          </div>

          <div className="hero-mini-metrics">
            <div>
              <strong>{formatCount(totalJobs)}</strong>
              <span>Total workload</span>
            </div>
            <div>
              <strong>{formatCount(workers)}</strong>
              <span>Workers active</span>
            </div>
            <div>
              <strong>{formatCount(queues)}</strong>
              <span>Queues managed</span>
            </div>
          </div>
        </div>

        <div className="hero-visual card glass">
          <img src={HERO_IMAGE} alt="QueueForge visual" />
          <div className="hero-overlay">
            <div className="overlay-card">
              <span className="overlay-kicker">Control mood</span>
              <strong>Pastel precision</strong>
              <p>Elegant orchestration with soft visual hierarchy.</p>
            </div>
          </div>
        </div>
      </section>

      <section className="metrics-grid">
        {metricCards.map((card) => (
          <article key={card.label} className={`metric-card metric-${card.tone}`}>
            <span>{card.label}</span>
            <strong>{formatCount(card.value)}</strong>
          </article>
        ))}
      </section>

      <section className="content-grid">
        <div className="left-stack">
          <div className="card glass section-card">
            <div className="section-head">
              <div>
                <span className="section-chip">Operational canvas</span>
                <h3>Flow quality snapshot</h3>
              </div>
            </div>

            <div className="progress-list">
              <div className="progress-item">
                <div className="progress-top">
                  <span>Queued load</span>
                  <b>{queuedPct}%</b>
                </div>
                <div className="progress-track">
                  <div style={{ width: `${queuedPct}%` }} />
                </div>
              </div>

              <div className="progress-item">
                <div className="progress-top">
                  <span>Completion rate</span>
                  <b>{completedPct}%</b>
                </div>
                <div className="progress-track">
                  <div style={{ width: `${completedPct}%` }} />
                </div>
              </div>

              <div className="progress-item">
                <div className="progress-top">
                  <span>Recovery pressure</span>
                  <b>{retryPct}%</b>
                </div>
                <div className="progress-track">
                  <div style={{ width: `${retryPct}%` }} />
                </div>
              </div>
            </div>
          </div>

          <div className="card glass section-card">
            <div className="section-head">
              <div>
                <span className="section-chip">Insight ribbons</span>
                <h3>What the system is saying</h3>
              </div>
            </div>

            <div className="notes-grid">
              {controlNotes.map((note) => (
                <div key={note.title} className="note-card">
                  <small>{note.title}</small>
                  <strong>{note.value}</strong>
                  <p>{note.copy}</p>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="right-stack">
          <div className="card image-story">
            <img src={ACCENT_IMAGE} alt="Pastel dashboard inspiration" />
            <div className="image-story-overlay">
              <span className="section-chip">Visual command lane</span>
              <h3>Soft visuals, sharp operations.</h3>
              <p>
                A dashboard that feels editorial, premium and different — not
                just another admin panel.
              </p>
            </div>
          </div>

          <div className="card glass section-card compact">
            <div className="section-head">
              <div>
                <span className="section-chip">Core lanes</span>
                <h3>System priorities</h3>
              </div>
            </div>

            <ul className="priority-list">
              <li>
                <span>01</span>
                <div>
                  <strong>Keep queues visible</strong>
                  <p>Every backlog state surfaced immediately.</p>
                </div>
              </li>
              <li>
                <span>02</span>
                <div>
                  <strong>Protect recovery paths</strong>
                  <p>Retry + DLQ signals stay readable and contained.</p>
                </div>
              </li>
              <li>
                <span>03</span>
                <div>
                  <strong>Scale worker rhythm</strong>
                  <p>Workers and queues viewed as one coordinated fleet.</p>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </section>
    </div>
  );
}
