import React, { Suspense } from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx';
import CircularProgress from '@mui/material/CircularProgress';
import { Provider } from 'react-redux';
import { store } from './store/store.js';
import './i18n/i18n.js';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
	<React.StrictMode>
		<Suspense fallback={<CircularProgress sx={{ position: 'absolute', left: '49%', top: '50%' }} />}>
			<Provider store={store}>
				<App />
			</Provider>
		</Suspense>
	</React.StrictMode>
);
