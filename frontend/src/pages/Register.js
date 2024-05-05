import React, { useState } from 'react';

export default function Register() {
    // Stany dla przechowywania wartości wprowadzonych przez użytkownika
    const [firstname, setFirstname] = useState('');
    const [lastname, setLastname] = useState('');
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');

    // Funkcje do obsługi zmiany wartości w polach formularza
    const handleFirstnameChange = (event) => {
        setFirstname(event.target.value);
    };

    const handleLastnameChange = (event) => {
        setLastname(event.target.value);
    };

    const handleLoginChange = (event) => {
        setLogin(event.target.value);
    };

    const handlePasswordChange = (event) => {
        setPassword(event.target.value);
    };

    // Funkcja do obsługi wysłania formularza
    const handleSubmit = (event) => {
        event.preventDefault();
        // Tutaj możesz wykonać akcje związane z wysłaniem danych do backendu
        console.log('Firstname:', firstname);
        console.log('Lastname:', lastname);
        console.log('Login:', login);
        console.log('Password:', password);
    };

    return (
        <form onSubmit={handleSubmit}>
            <div>
                <label htmlFor="firstname">Firstname:</label>
                <input
                    type="text"
                    id="firstname"
                    value={firstname}
                    onChange={handleFirstnameChange}
                />
            </div>
            <div>
                <label htmlFor="lastname">Lastname:</label>
                <input
                    type="text"
                    id="lastname"
                    value={lastname}
                    onChange={handleLastnameChange}
                />
            </div>
            <div>
                <label htmlFor="login">Login:</label>
                <input
                    type="text"
                    id="login"
                    value={login}
                    onChange={handleLoginChange}
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
            <button type="submit">Register</button>
        </form>
    );
}
