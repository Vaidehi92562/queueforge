package com.queueforge.organization;

import com.queueforge.security.CurrentUserService;
import com.queueforge.user.User;
import com.queueforge.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final CurrentUserService currentUserService;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            OrganizationMemberRepository organizationMemberRepository,
            CurrentUserService currentUserService
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        String normalizedName = request.getName().trim();

        if (organizationRepository.existsByNameAndOwnerId(normalizedName, currentUser.getId())) {
            throw new IllegalArgumentException("Organization with this name already exists for the current user");
        }

        Organization organization = new Organization();
        organization.setName(normalizedName);
        organization.setDescription(request.getDescription());
        organization.setOwner(currentUser);

        Organization savedOrganization = organizationRepository.save(organization);

        OrganizationMember member = new OrganizationMember(
                savedOrganization,
                currentUser,
                UserRole.ADMIN
        );

        organizationMemberRepository.save(member);

        return toResponse(savedOrganization);
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponse> getMyOrganizations() {
        User currentUser = currentUserService.getCurrentUser();

        return organizationMemberRepository.findByUserId(currentUser.getId())
                .stream()
                .map(OrganizationMember::getOrganization)
                .map(this::toResponse)
                .toList();
    }

    private OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getDescription(),
                organization.getOwner().getId(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }
}
