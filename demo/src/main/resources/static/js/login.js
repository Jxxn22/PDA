// Validar solo números en el campo de documento
document.getElementById('numeroDocumento').addEventListener('input', function(e) {
    this.value = this.value.replace(/\D/g, '');
});

// Manejar el envío del formulario
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const numeroDocumento = document.getElementById('numeroDocumento').value;
    const contrasena = document.getElementById('contrasena').value;
    const errorMessage = document.getElementById('errorMessage');

    // Validaciones básicas
    if (!numeroDocumento || !contrasena) {
        mostrarError('Por favor, complete todos los campos');
        return;
    }

    if (numeroDocumento.length < 6) {
        mostrarError('El número de documento debe tener al menos 6 dígitos');
        return;
    }

    try {
        // Llamada al backend para validar credenciales
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                numeroDocumento: numeroDocumento,
                contrasena: contrasena
            })
        });

        if (response.ok) {
            const data = await response.json();
            // Guardar información de sesión
            sessionStorage.setItem('usuario', JSON.stringify(data));
            // Redirigir a la página principal
            window.location.href = '/inicio';
        } else {
            const error = await response.text();
            mostrarError(error || 'Credenciales incorrectas');
        }
    } catch (error) {
        console.error('Error de conexión:', error);
        mostrarError('Error de conexión. Por favor, intente nuevamente.');
    }
});

function mostrarError(mensaje) {
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.textContent = mensaje;
    errorMessage.classList.add('show');
    
    // Ocultar el mensaje después de 5 segundos
    setTimeout(() => {
        errorMessage.classList.remove('show');
    }, 5000);
}

function cerrarLogin() {
    window.close();
}