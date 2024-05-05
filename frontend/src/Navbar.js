import {Link, useMatch, useResolvedPath} from "react-router-dom";

export default function Navbar(){
    return <nav className="nav">
        <Link to="/" className="site-title">
            FILWMEB
        </Link>
        <ul>
            <CustomLink to="/movies">Movies</CustomLink>
            <CustomLink to="/recommended">Recommended for You!</CustomLink>
            <CustomLink to="/login">Log in</CustomLink>
            <CustomLink to="/register">Register</CustomLink>
        </ul>
    </nav>
}

function CustomLink({to, children, ...props}){
    const resolvedPath = useResolvedPath(to)
    const isActive = useMatch({path: resolvedPath.pathname, end: true})
    return (
        <li className={isActive ? "active" : ""}>
            <Link to={to} {...props}>
                {children}
            </Link>
        </li>
    )
}