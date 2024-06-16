import { movies } from './movies';

export const moviesApi = movies.injectEndpoints({
	endpoints: (builder) => ({
		getMoviesByQuery: builder.query({
			query: (args) => {
				const { query } = args;
				return `search/movie?query=${query.replace(
					' ',
					'%20'
				)}`;
			},
		}),
		getMovieDetailsById: builder.query({
			query: (args) => {
				const { id } = args;
				return `movies/${id}`;
			},
		}),
		getMoviesByPage: builder.query({
			query: ({ page }) => `/movies/page/${page}`,
		}),
		getAllMovies: builder.query({
			query: () => `/movies`,
		}),
	}),
	overrideExisting: false,
});

export const {
	useGetMoviesByQueryQuery,
	useGetMovieDetailsByIdQuery,
	useGetMoviesByPageQuery,
	useGetAllMoviesQuery
} = moviesApi;
