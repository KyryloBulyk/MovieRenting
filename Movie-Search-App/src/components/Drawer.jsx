import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import MovieCard from './MovieCard';
import { useSelector, useDispatch } from 'react-redux';
import { toggleDrawer } from '../features/drawer/drawerSlice';
import { useTranslation } from 'react-i18next';
import CustomizedSnackbar from './Snackbar';

function TemporaryDrawer() {
	const drawerMode = useSelector((state) => state.drawer.isOpen);
	const moviesData = useSelector((state) => state.favoriteMovies.movies);
	const dispatch = useDispatch();
	const { t } = useTranslation();

	const onCloseDrawer = () => {
		dispatch(toggleDrawer());
		// window.location.reload(true);
	};

	return (
		<Drawer anchor='left' open={drawerMode} onClose={() => dispatch(toggleDrawer())}>
			<CustomizedSnackbar />
			<Box
				sx={{ width: 600, height: '1000%', background: '#303030', padding: '10px 5px 0 10px' }}
				role='presentation'
			>
				<header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
					<Typography variant='h5' sx={{ color: '#fff' }}>
						{t('drawerLogoName')}
					</Typography>
					<IconButton sx={{ color: '#fff' }} onClick={onCloseDrawer}>
						<CloseIcon sx={{ width: '27px', height: '27px' }} />
					</IconButton>
				</header>
				<main style={{ display: 'flex', gap: '30px', padding: '30px 20px', flexWrap: 'wrap' }}>
					{moviesData.map((m) => (
						<MovieCard key={m.id} data={m} favorite={true} type='favorite' />
					))}
				</main>
			</Box>
		</Drawer>
	);
}

export default TemporaryDrawer;
