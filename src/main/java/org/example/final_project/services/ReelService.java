package org.example.final_project.services;



import org.example.final_project.models.Reels;

import java.util.List;

public interface ReelService {
	
	public Reels createReels(Reels reel);
	
	public void deleteReels(Integer reelId);
	
	public void editReels(Reels reel);
	
	public List<Reels> getAllReels();

}
