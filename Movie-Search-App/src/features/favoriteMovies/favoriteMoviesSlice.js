import { createSlice } from '@reduxjs/toolkit';

const initialState = {
	movies: JSON.parse(localStorage.getItem('favoriteMovies')) || [],
};

const favoriteMoviesSlice = createSlice({
	name: 'favoriteMovies',
	initialState,
	reducers: {
		addMovie: (state, action) => {
			state.movies.push(action.payload);
			localStorage.setItem('favoriteMovies', JSON.stringify(state.movies));
		},
		removeMovie: (state, action) => {
			const filteredMovies = state.movies.filter((m) => m.id !== action.payload);
			state.movies = filteredMovies;
			localStorage.setItem('favoriteMovies', JSON.stringify(filteredMovies));
		},
	},
});

export const { addMovie, removeMovie } = favoriteMoviesSlice.actions;
export default favoriteMoviesSlice.reducer;
