import React, { useState } from 'react';
//import './App.css';

function App() {
  const [ingredients, setIngredients] = useState('');
  const [recipes, setRecipes] = useState([]);

  const fetchRecipes = async () => {
    try{
    const query = ingredients.split(',').map(i => i.trim()).join(',');
    const response = await fetch(`http://localhost:8080/api/recipes?ingredients=${query}`);
    const data = await response.json();
    setRecipes(data);
    //console.log(data);
    }catch (error){
      console.error("Error fetching recipes:", error);
    }
  };


  return (
    <div style={{ padding: '2rem', fontFamily: 'Arial, sans-serif' }}>
      <h1 style={{ fontSize: '2rem', marginBottom: '1rem'}} >Recipe Finder </h1>

      <div style={{ marginBottom: '1.5rem' }}>
      <input
        type="text"
        value={ingredients}
        onChange={e => setIngredients(e.target.value)}
        placeholder="e.g. rice, shrimp, tomato"
        style={{ 
          padding: '0.5rem',
          width: 300, 
          marginRight: '10px',
          borderRadius: '5px',
          border: '1px solid #cc'   
        }}
      />
      <button
       onClick={fetchRecipes}
       style={{
        padding: '0.5rem 1rem',
        backgroundColor: '#4CAF50',
        color: 'white',
        border: 'none',
        borderRadius: '5px',
        cursor: 'pointer'
       }}
       >
        Search
        </button>
       </div>

       <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
          gap: '1.5rem'
       }}>
   
        {recipes.map((recipe, idx) => (
          <div key={idx} style={{ 
            border: '1px solid #ddd', 
            borderRadius: '8px',
            padding: 'irem',
            boxShadow: '0 2px 5px rgba(0,0,0,0.1)', 
            }}>

            <h2>{recipe.title}</h2>

            {recipe.imageUrl ? ( 
            <img 
              src={recipe.imageUrl} 
              alt={recipe.title} 
              style={{ width: '100%', borderRadius: '5px'}}
            />
            ) : (
              <div style={{
                width: '100%',
                height: '200px',
                backgroundColor: '#eee',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                borderRadius: '5px'
              }}>
                <span>No Image</span>
              </div>
            )}

            <p><strong>Website:</strong> <a href={recipe.website} target="_blank" rel="noopener noreferrer">{recipe.website}</a></p>
           {recipe.rating && <p><strong>Rating:</strong> {recipe.rating}</p>}
            <p><strong>Ingredients:</strong></p>

            <ul>
              {recipe.ingredients.map((ing, i) => <li key={i}>{ing}</li>)}
              </ul>
            </div>
          ))}
        </div>
      </div>
  );
}

export default App;
