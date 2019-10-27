package com.cloud.crypted.server.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud.crypted.server.core.models.RecoveryInformation;

public interface RecoveryInformationRepository extends JpaRepository<RecoveryInformation, Long> { }