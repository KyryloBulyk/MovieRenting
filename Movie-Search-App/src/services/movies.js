import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

export const movies = createApi({
	reducerPath: 'moviesApi',
	baseQuery: fetchBaseQuery({
		baseUrl: 'http://localhost:8080/ram/',
		headers: {
			accept: 'application/json',
		},
	}),
	endpoints: () => ({}),
});
