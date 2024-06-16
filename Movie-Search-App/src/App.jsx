import Header from './components/Header';
import TemporaryDrawer from './components/Drawer';
import MovieList from './components/MovieList';
import SearchBlock from './components/SearchBlock';
import { useState } from 'react';

function App() {
	const [query, setQuery] = useState('');

	return (
		<div>
			<Header />
			<TemporaryDrawer />
			<main style={{ paddingTop: '100px' }}>
				<SearchBlock setQuery={setQuery} />
				<section className='movies'>
					<MovieList query={query} />
				</section>
			</main>
		</div>
	);
}

export default App;
