import { configureStore } from '@reduxjs/toolkit';
import { setupListeners } from '@reduxjs/toolkit/query';
import { movies } from '../services/movies';
import drawerReducer from '../features/drawer/drawerSlice';
import favoriteMoviesReducer from '../features/favoriteMovies/favoriteMoviesSlice';
import modeReducer from '../features/mode/modeSlice';
import sortingReducer from '../features/sorting/sortingSlice';

export const store = configureStore({
	reducer: {
		[movies.reducerPath]: movies.reducer,
		drawer: drawerReducer,
		favoriteMovies: favoriteMoviesReducer,
		mode: modeReducer,
		sorting: sortingReducer,
	},
	middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(movies.middleware),
});

setupListeners(store.dispatch);
