package com.cloud.crypted.server.core.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud.crypted.server.core.models.File;

public interface FileRepository extends JpaRepository<File, Long> {
	
	boolean existsByCloudFileID(String cloudFileID);
	
	Optional<File> findByCloudFileID(String cloudFileID);
	
	void deleteByCloudFileID(String cloudFileID);
	
}