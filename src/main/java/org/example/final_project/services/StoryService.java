package org.example.final_project.services;



import org.example.final_project.exceptions.StoryException;
import org.example.final_project.exceptions.UserException;
import org.example.final_project.models.Story;

import java.util.List;

public interface StoryService {

	public Story createStory(Story story, Long userId) throws UserException;
	
	public List<Story> findStoryByUserId(Long userId) throws UserException, StoryException;
	
	
}
