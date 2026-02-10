const Cookies = {
    get(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) {
            return parts.pop().split(';').shift();
        }
        return undefined;
    },
    set(name, value, options = {}) {
        let cookie = `${encodeURIComponent(name)}=${encodeURIComponent(value)}`;
        if (options.expires) {
            const expires = typeof options.expires === 'number'
                ? new Date(Date.now() + options.expires * 864e5)
                : options.expires;
            cookie += `; expires=${expires.toUTCString()}`;
        }
        if (options.path) cookie += `; path=${options.path}`;
        document.cookie = cookie;
    },
    remove(name, options = {}) {
        this.set(name, '', { ...options, expires: -1 });
    }
};

export default Cookies;
