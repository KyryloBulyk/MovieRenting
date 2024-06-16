import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import Backdrop from '@mui/material/Backdrop';
import Fade from '@mui/material/Fade';
import CircularProgress from '@mui/material/CircularProgress';
import { useTranslation } from 'react-i18next';

const style = {
	position: 'absolute',
	top: '50%',
	left: '50%',
	transform: 'translate(-50%, -50%)',
	width: 900,
	bgcolor: 'background.paper',
	boxShadow: 24,
	p: 2,
	display: 'flex',
	borderRadius: '10px',
	gap: '20px',
};

const staticMovieData = {
	title: "Don't found movie",
	poster_path: "/9U9QmbCDIBhqDShuIxOiS9gjKYz.jpg",
	release_date: "2023-01-01",
	overview: "The film was not found",
};

function BasicModal({ active, setActive, id }) {
	const handleClose = () => setActive(false);
	const { t, i18n } = useTranslation();
	const [data, setData] = useState(null);
	const [isLoading, setIsLoading] = useState(false);
	const [error, setError] = useState(null);

	useEffect(() => {
		const fetchMovieDetails = async () => {
			setIsLoading(true);
			try {
				const response = await fetch(`http://localhost:8080/ram/movies/${id}`);
				if (response.status === 404) {
					setData(staticMovieData);
				} else if (!response.ok) {
					throw new Error('Network response was not ok');
				} else {
					const movieData = await response.json();
					setData(movieData);
				}
			} catch (error) {
				setError(error.message);
			} finally {
				setIsLoading(false);
			}
		};

		if (active) {
			fetchMovieDetails();
		}
	}, [active, id, i18n.language]);

	return (
		<Modal
			aria-labelledby='transition-modal-title'
			aria-describedby='transition-modal-description'
			open={active}
			onClose={handleClose}
			closeAfterTransition
			slots={{ backdrop: Backdrop }}
			slotProps={{
				backdrop: {
					timeout: 500,
				},
			}}
		>
			<Fade in={active}>
				<Box sx={style}>
					{error && <Typography variant='h3'>Error: {error}</Typography>}
					{isLoading ? (
						<CircularProgress />
					) : (
						data && (
							<>
								<div>
									<img
										src={`https://image.tmdb.org/t/p/w300/${data.poster_path}`}
										alt={data.title}
										style={{ maxHeight: 400 }}
									/>
								</div>
								<div>
									<Typography variant='h3' component='h1' fontWeight={600}>
										{data.title}
									</Typography>
									<div style={{ display: 'flex', alignItems: 'center', gap: '10px', paddingTop: '20px' }}>
									</div>
									<Typography variant='h6' component='p'>
										{t('modal.releaseDate')} {data.release_date}
									</Typography>
									<Typography sx={{ paddingTop: '10px' }}>{data.overview}</Typography>
								</div>
							</>
						)
					)}
				</Box>
			</Fade>
		</Modal>
	);
}

export default BasicModal;