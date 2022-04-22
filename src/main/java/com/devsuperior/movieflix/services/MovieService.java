package com.devsuperior.movieflix.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.dto.MovieCardDTO;
import com.devsuperior.movieflix.dto.MovieDetailsDTO;
import com.devsuperior.movieflix.entities.Genre;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.repositories.GenreRepository;
import com.devsuperior.movieflix.repositories.MovieRepository;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;
import com.devsuperior.movieflix.services.exceptions.UnauthorizedException;

@Service
public class MovieService {

	@Autowired
	private MovieRepository repository;
	
	@Autowired
	private GenreRepository genreRepository;
	
	@Autowired
	private AuthService authService;

	@Transactional(readOnly = true)
	public MovieDetailsDTO findById(Long id) {
		Optional<Movie> obj = repository.findById(id);
		Movie entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new MovieDetailsDTO(entity);
	}
	
	@Transactional(readOnly = true)
	public Page<MovieCardDTO> findByGenre(Long genreId, Pageable pageable) {
		
		authService.authenticated();
		
		try {
			List<Genre> genres = (genreId == 0) ? null : Arrays.asList(genreRepository.getOne(genreId));
			//genres = genreRepository.findAll(Sort.by("title"));
		    Page<Movie> page = repository.find(genres, pageable);
		    repository.findMovies(page.getContent());
		 	return page.map(x -> new MovieCardDTO(x));
		 	
		} catch(UnauthorizedException e) {
			throw new ResourceNotFoundException("Unauthorized exception");
		}
	}
}
