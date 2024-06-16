import { createSlice } from '@reduxjs/toolkit';

const initialState = {
	value: '',
};

const sortingSlice = createSlice({
	name: 'sorting',
	initialState,
	reducers: {
		setSorting(state, action) {
			state.value = action.payload;
		},
	},
});

export const { setSorting } = sortingSlice.actions;
export default sortingSlice.reducer;
