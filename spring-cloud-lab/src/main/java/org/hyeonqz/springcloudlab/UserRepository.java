package org.hyeonqz.springcloudlab;

import org.hyeonqz.springcloudlab.vault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
