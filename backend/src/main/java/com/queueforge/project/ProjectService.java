package com.queueforge.project;

import com.queueforge.organization.Organization;
import com.queueforge.organization.OrganizationMemberRepository;
import com.queueforge.organization.OrganizationRepository;
import com.queueforge.security.CurrentUserService;
import com.queueforge.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final CurrentUserService currentUserService;

    public ProjectService(
            ProjectRepository projectRepository,
            OrganizationRepository organizationRepository,
            OrganizationMemberRepository organizationMemberRepository,
            CurrentUserService currentUserService
    ) {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        validateOrganizationAccess(request.getOrganizationId(), currentUser.getId());

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        String normalizedName = request.getName().trim();

        if (projectRepository.existsByNameAndOrganizationId(normalizedName, organization.getId())) {
            throw new IllegalArgumentException("Project with this name already exists in this organization");
        }

        Project project = new Project();
        project.setName(normalizedName);
        project.setDescription(request.getDescription());
        project.setOrganization(organization);

        Project savedProject = projectRepository.save(project);

        return toResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByOrganization(UUID organizationId) {
        User currentUser = currentUserService.getCurrentUser();

        validateOrganizationAccess(organizationId, currentUser.getId());

        return projectRepository.findByOrganizationId(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateOrganizationAccess(UUID organizationId, UUID userId) {
        boolean isMember = organizationMemberRepository.existsByOrganizationIdAndUserId(
                organizationId,
                userId
        );

        if (!isMember) {
            throw new IllegalArgumentException("You do not have access to this organization");
        }
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getOrganization().getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
