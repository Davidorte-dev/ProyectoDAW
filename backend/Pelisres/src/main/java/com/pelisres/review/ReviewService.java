package com.pelisres.review;

import com.pelisres.user.User;
import com.pelisres.user.UserRepository;
import com.pelisres.movie.Movie;
import com.pelisres.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importa @Transactional
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;


        public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
            .map(ReviewResponse::new)
            .collect(Collectors.toList());
    }


    @Transactional
    public void saveReview(ReviewRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Movie movie = movieRepository.findByIdPelicula(request.getId_pelicula())
                .orElseGet(() -> {
                    Movie newMovie = new Movie();
                    newMovie.setIdPelicula(request.getId_pelicula());
                    newMovie.setTitulo(request.getTitulo());
                    newMovie.setImagenUrl(request.getImagen_url());
                    newMovie.setDescripcion(request.getDescripcion_pelicula());
                    return movieRepository.save(newMovie);
                });

        

        Review review = Review.builder()
                .texto(request.getTexto())
                .valoracion(request.getValoracion())
                .fecha_creacion(LocalDateTime.now())
                .user(user)
                .movie(movie)
                .build();

        reviewRepository.save(review);
        
    }
}