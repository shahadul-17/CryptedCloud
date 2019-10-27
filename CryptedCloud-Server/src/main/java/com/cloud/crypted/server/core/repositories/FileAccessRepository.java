package com.cloud.crypted.server.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud.crypted.server.core.models.FileAccess;
import com.cloud.crypted.server.core.models.FileAccessID;

public interface FileAccessRepository extends JpaRepository<FileAccess, FileAccessID> {
	
	List<FileAccess> findByFileAccessIDUserID(long userID);
	List<FileAccess> findByFileAccessIDFileID(long fileID);
	
	void deleteByFileAccessIDUserID(long userID);
	void deleteByFileAccessIDFileID(long fileID);
	
}