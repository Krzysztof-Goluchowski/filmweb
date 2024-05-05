import Navbar from "./Navbar";
import Movies from "./pages/Movies";
import Home from "./pages/home";
import Recommended from "./pages/Recommended";
import Login from "./pages/Login";
import Register from "./pages/Register";
import { Route, Routes } from "react-router-dom";

function App(){
  return (
      <>
        <Navbar/>
        <div className="conteiner">
          <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/movies" element={<Movies />} />
              <Route path="/recommended" element={<Recommended />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
          </Routes>
        </div>
      </>
  )


}

export default App