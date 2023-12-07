package tech.chillo.avis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Set;

@AllArgsConstructor
public enum TypeDeRole {
    USER(
        Set.of(TypeDePermission.UTILISATEUR_CREATE_AVIS)
    ),
    ADMIN(
            Set.of(
                    TypeDePermission.ADMIN_UPDATE,
                    TypeDePermission.ADMIN_READ,
                    TypeDePermission.ADMIN_DELETE,
                    TypeDePermission.ADMIN_CREATE,

                    TypeDePermission.MANAGER_CREATE,
                    TypeDePermission.MANAGER_READ,
                    TypeDePermission.MANAGER_DELETE_AVIS,
                    TypeDePermission.MANAGER_UPDATE
            )
    ),
    MANAGER(
            Set.of(
                    TypeDePermission.MANAGER_CREATE,
                    TypeDePermission.MANAGER_READ,
                    TypeDePermission.MANAGER_DELETE_AVIS,
                    TypeDePermission.MANAGER_UPDATE
            )
    );

    @Getter
    Set<TypeDePermission> permissions;
}
