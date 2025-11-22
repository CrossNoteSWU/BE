package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.UserProfileFullResponseDto;
import com.swulion.crossnote.service.CustomUserDetails;
import com.swulion.crossnote.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class ProfileController {

	private final ProfileService profileService;

	@GetMapping("/{userId}/profile")
	public ResponseEntity<UserProfileFullResponseDto> getProfile(
			@PathVariable("userId") Long userId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		int safePage = Math.max(page, 0);
		int safeSize = Math.min(Math.max(size, 1), 50);
		Pageable pageable = PageRequest.of(safePage, safeSize);
		Long currentUserId = (userDetails != null && userDetails.getUser() != null)
				? userDetails.getUser().getUserId() : null;
		return ResponseEntity.ok(profileService.getUserProfileFull(currentUserId, userId, pageable));
	}
}


