import Cookies from "./lib/cookies.js";

export default function LogoutScript() {
    Cookies.remove('token');
    Cookies.remove('username');
    window.location.href = '/';
}