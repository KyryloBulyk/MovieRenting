import FoundMovies from './FoundMovies';

function MovieList({ query }) {
	return (
		<div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
			<FoundMovies query={query} />
		</div>
	);
}

export default MovieList;
