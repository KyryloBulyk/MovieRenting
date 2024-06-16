import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import { forwardRef } from 'react';
import { useTranslation } from 'react-i18next';

const Alert = forwardRef(function Alert(props, ref) {
	return <MuiAlert elevation={6} ref={ref} variant='filled' {...props} />;
});

function CustomizedSnackbar({ active, setActive, type }) {
	const { t } = useTranslation();

	const handleClose = (event, reason) => {
		if (reason === 'clickaway') {
			return;
		}

		setActive(false);
	};
	return (
		<Snackbar open={active} autoHideDuration={2000} onClose={handleClose}>
			<Alert onClose={handleClose} severity={type} sx={{ width: '100%' }}>
				{type === 'success' ? t('snackbar.added') : t('snackbar.alreadyAdded')}
			</Alert>
		</Snackbar>
	);
}

export default CustomizedSnackbar;
