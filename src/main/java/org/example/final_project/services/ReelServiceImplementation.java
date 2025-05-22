//package org.example.final_project.services;
//
//
//import org.example.final_project.models.Reels;
//import org.example.final_project.repositories.ReelRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ReelServiceImplementation implements ReelService {
//
//	@Autowired
//	private ReelRepository reelRepository;
//
//	@Override
//	public Reels createReels(Reels reel) {
//
//		return reelRepository.save(reel);
//	}
//
//
//	@Override
//	public void deleteReels(Integer reelId) {
//		reelRepository.deleteById(reelId);
//
//	}
//
//	@Override
//	public void editReels(Reels reel) {
//		reelRepository.save(reel);
//
//	}
//
//
//	@Override
//	public List<Reels> getAllReels() {
//		List<Reels> reels=reelRepository.findAll();
//		return reels;
//	}
//
//}
