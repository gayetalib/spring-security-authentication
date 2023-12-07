package tech.chillo.avis.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TypeDePermission {
    ADMIN_CREATE,
    ADMIN_UPDATE,
    ADMIN_READ,
    ADMIN_DELETE,


    MANAGER_CREATE,
    MANAGER_UPDATE,
    MANAGER_READ,
    MANAGER_DELETE_AVIS,

    UTILISATEUR_CREATE_AVIS;

    private String permission;
}
