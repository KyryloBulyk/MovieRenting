import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import { useEffect, useState } from 'react';
import { toggleDrawer } from '../features/drawer/drawerSlice';
import { useDispatch } from 'react-redux';
import { useTranslation } from 'react-i18next';
import LightModeIcon from '@mui/icons-material/LightMode';
import DarkModeIcon from '@mui/icons-material/DarkMode';
import { useSelector } from 'react-redux';
import { toggleMode } from '../features/mode/modeSlice';
import HideOnScroll from '../utils/HideOnScroll';

function Header(props) {
	const mode = useSelector((state) => state.mode.value);
	const { t, i18n } = useTranslation();
	const [language, setLanguage] = useState('en');
	const dispatch = useDispatch();

	useEffect(() => {
		// Ensure the language is set to English
		const language = 'en';
		setLanguage(language);
		i18n.changeLanguage(language);
	}, []);

	return (
		<HideOnScroll {...props}>
			<AppBar>
				<Toolbar sx={{ justifyContent: 'space-between' }}>
					<IconButton
						size='large'
						edge='start'
						color='inherit'
						aria-label='menu'
						onClick={() => dispatch(toggleDrawer())}
					>
					<MenuIcon />
					</IconButton>
					<Typography variant='h5' component='div' sx={{ flexGrow: 1, textAlign: 'center' }}>
						{t('logoName')}
					</Typography>
					<IconButton onClick={() => dispatch(toggleMode())}>
						{mode === 'dark' ? (
							<LightModeIcon sx={{ color: '#fff' }} />
						) : (
							<DarkModeIcon sx={{ color: '#fff' }} />
						)}
					</IconButton>
				</Toolbar>
			</AppBar>
		</HideOnScroll>
	);
}

export default Header;