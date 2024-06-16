import { useGetMoviesByQueryQuery, useGetMoviesByPageQuery, useGetAllMoviesQuery } from '../services/moviesApi';
import CircularProgress from '@mui/material/CircularProgress';
import MovieCard from './MovieCard';
import ErrorText from './ErrorText';
import Pagination from '@mui/material/Pagination';
import { useState, useEffect } from 'react';
import { skipToken } from '@reduxjs/toolkit/query';

function FoundMovies({ query }) {
	const [page, setPage] = useState(1);
	const [totalPages, setTotalPages] = useState(1);

	const { data: queryData, error: queryError, isLoading: queryLoading } = useGetMoviesByQueryQuery(query ? { query, page } : skipToken);
	const { data: pageData, error: pageError, isLoading: pageLoading } = useGetMoviesByPageQuery({ page });
	const {data: allData, error: allError, isLoading: allLoading} = useGetAllMoviesQuery();

	const data = query ? queryData : pageData;
	const error = query ? queryError : pageError;
	const isLoading = query ? queryLoading : pageLoading;

	// setTotalPages(allData.length / 10);

	useEffect(() => {
		if (data) {
			console.log(data);
		}

		if(allData) {
			console.log(allData);
			setTotalPages(allData.length / 10);
		}
	}, [data, allData]);

	const handleClickScroll = () => {
		window.scrollTo({ top: 0, left: 0, behavior: 'smooth' });
	};

	if (isLoading) {
		return <CircularProgress />;
	}

	if (error || !data || (Array.isArray(data) && data.length === 0)) {
		return <ErrorText />;
	}

	return (
		<>
			<div className='movies'>
				{data.slice(0, 10).map((m) => (
					<MovieCard key={m.id} data={m} />
				))}
			</div>
			<div style={{ padding: '30px 0' }}>
				<Pagination
					sx={{ background: '#adbdd4', borderRadius: '5px', padding: '5px' }}
					count={totalPages.toFixed(0)}
					color='primary'
					page={page}
					onChange={(e, value) => {
						handleClickScroll();
						setPage(value);
					}}
				/>
			</div>
		</>
	);
}

export default FoundMovies;