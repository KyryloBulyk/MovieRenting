import { Box } from '@mui/material';
import Paper from '@mui/material/Paper';
import InputBase from '@mui/material/InputBase';
import IconButton from '@mui/material/IconButton';
import SearchIcon from '@mui/icons-material/Search';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useDispatch, useSelector } from 'react-redux';
import { setSorting } from '../features/sorting/sortingSlice';

const boxStyle = {
	background: '#6fa2f0',
	width: '100%',
	maxWidth: '1330px',
	height: '70px',
	margin: '40px auto',
	borderRadius: '5px',
	display: 'flex',
	justifyContent: 'center',
	alignItems: 'center',
	padding: '0 20px',
};

function SearchBlock({ setQuery }) {
	const [searchValue, setSearchValue] = useState('');
	const { t } = useTranslation();

	const onSubmitHandler = (e) => {
		e.preventDefault();
		if (!searchValue.trim()) {
			return;
		}
		setQuery(searchValue);
	};

	const onClickHandler = () => {
		if (!searchValue.trim()) {
			return;
		}
		setQuery(searchValue);
	};

	return (
		<Box sx={boxStyle}>
			<Paper
				component='form'
				sx={{ p: '2px 4px', display: 'flex', alignItems: 'center', width: 700 }}
				onSubmit={onSubmitHandler}
			>
				<InputBase
					sx={{ ml: 1, flex: 1 }}
					placeholder={t('searchBar.placeholder')}
					value={searchValue}
					onChange={(e) => setSearchValue(e.target.value)}
				/>
				<IconButton type='button' sx={{ p: '10px' }} onClick={onClickHandler}>
					<SearchIcon />
				</IconButton>
			</Paper>
		</Box>
	);
}

export default SearchBlock;
