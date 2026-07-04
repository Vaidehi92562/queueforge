package com.queueforge.bootstrap;

import com.queueforge.organization.Organization;
import com.queueforge.organization.OrganizationMember;
import com.queueforge.organization.OrganizationMemberRepository;
import com.queueforge.organization.OrganizationRepository;
import com.queueforge.project.Project;
import com.queueforge.project.ProjectRepository;
import com.queueforge.queue.JobQueue;
import com.queueforge.queue.JobQueueRepository;
import com.queueforge.retry.RetryPolicy;
import com.queueforge.retry.RetryPolicyRepository;
import com.queueforge.retry.RetryStrategy;
import com.queueforge.user.User;
import com.queueforge.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultWorkspaceProvisioner {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final ProjectRepository projectRepository;
    private final RetryPolicyRepository retryPolicyRepository;
    private final JobQueueRepository jobQueueRepository;

    public DefaultWorkspaceProvisioner(
            OrganizationRepository organizationRepository,
            OrganizationMemberRepository organizationMemberRepository,
            ProjectRepository projectRepository,
            RetryPolicyRepository retryPolicyRepository,
            JobQueueRepository jobQueueRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.projectRepository = projectRepository;
        this.retryPolicyRepository = retryPolicyRepository;
        this.jobQueueRepository = jobQueueRepository;
    }

    @Transactional
    public void provisionFor(User user) {
        Organization organization = createDefaultOrganization(user);
        Project project = createDefaultProject(organization);
        RetryPolicy retryPolicy = createOrReuseDefaultRetryPolicy();
        createDefaultQueue(project, retryPolicy);
    }

    private Organization createDefaultOrganization(User user) {
        String organizationName = "Personal Workspace";

        return organizationRepository.findAll()
                .stream()
                .filter(org -> org.getOwner() != null)
                .filter(org -> org.getOwner().getId().equals(user.getId()))
                .filter(org -> organizationName.equals(org.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Organization organization = new Organization();
                    organization.setName(organizationName);
                    organization.setDescription("Auto-created workspace for QueueForge onboarding.");
                    organization.setOwner(user);

                    Organization savedOrganization = organizationRepository.save(organization);

                    OrganizationMember member = new OrganizationMember(
                            savedOrganization,
                            user,
                            UserRole.ADMIN
                    );

                    organizationMemberRepository.save(member);

                    return savedOrganization;
                });
    }

    private Project createDefaultProject(Organization organization) {
        String projectName = "Default Project";

        return projectRepository.findByOrganizationId(organization.getId())
                .stream()
                .filter(project -> projectName.equals(project.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Project project = new Project();
                    project.setName(projectName);
                    project.setDescription("Auto-created project for job scheduling experiments.");
                    project.setOrganization(organization);

                    return projectRepository.save(project);
                });
    }

    private RetryPolicy createOrReuseDefaultRetryPolicy() {
        String policyName = "Default Retry Policy";

        return retryPolicyRepository.findAll()
                .stream()
                .filter(policy -> policyName.equals(policy.getName()))
                .findFirst()
                .orElseGet(() -> {
                    RetryPolicy retryPolicy = new RetryPolicy();
                    retryPolicy.setName(policyName);
                    retryPolicy.setStrategy(RetryStrategy.EXPONENTIAL_BACKOFF);
                    retryPolicy.setMaxAttempts(3);
                    retryPolicy.setBaseDelaySeconds(10);
                    retryPolicy.setMaxDelaySeconds(300);

                    return retryPolicyRepository.save(retryPolicy);
                });
    }

    private void createDefaultQueue(Project project, RetryPolicy retryPolicy) {
        String queueName = "default-queue";

        boolean alreadyExists = jobQueueRepository.existsByNameAndProjectId(
                queueName,
                project.getId()
        );

        if (alreadyExists) {
            return;
        }

        JobQueue queue = new JobQueue();
        queue.setProject(project);
        queue.setRetryPolicy(retryPolicy);
        queue.setName(queueName);
        queue.setPriority(5);
        queue.setMaxConcurrency(5);
        queue.setPaused(false);
        queue.setRateLimitPerMinute(120);

        jobQueueRepository.save(queue);
    }
}
