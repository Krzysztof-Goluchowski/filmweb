import React, { useState } from 'react';

export default function Login() {
    // Stan dla przechowywania wartości wprowadzonych przez użytkownika
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    // Funkcje do obsługi zmiany wartości w polach formularza
    const handleUsernameChange = (event) => {
        setUsername(event.target.value);
    };

    const handlePasswordChange = (event) => {
        setPassword(event.target.value);
    };

    // Funkcja do obsługi wysłania formularza
    const handleSubmit = async (event) => {
        event.preventDefault();

        try {
            const response = await fetch('http://localhost:9000/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    login: username,
                    password: password
                })
            });

            if (response.ok) {
                // Zalogowano poprawnie
                alert('Zalogowano poprawnie');
            } else {
                // Nieprawidłowy login lub hasło
                alert('Nieprawidłowy login lub hasło');
            }
        } catch (error) {
            console.error('Błąd logowania:', error);
            // Obsłuż błąd
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <div>
                <label htmlFor="username">Username:</label>
                <input
                    type="text"
                    id="username"
                    value={username}
                    onChange={handleUsernameChange}
                />
            </div>
            <div>
                <label htmlFor="password">Password:</label>
                <input
                    type="password"
                    id="password"
                    value={password}
                    onChange={handlePasswordChange}
                />
            </div>
            <button type="submit">Login</button>
        </form>
    );
}