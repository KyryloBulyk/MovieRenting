import { createSlice } from '@reduxjs/toolkit';

const initialState = {
	value: 'dark',
};

const modeSlice = createSlice({
	name: 'mode',
	initialState,
	reducers: {
		toggleMode: (state) => {
			state.value === 'dark' ? (state.value = 'light') : (state.value = 'dark');
			document.body.className = state.value;
		},
	},
});

export const { toggleMode } = modeSlice.actions;
export default modeSlice.reducer;
