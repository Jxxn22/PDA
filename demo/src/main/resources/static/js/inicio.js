// Verificar autenticación
window.addEventListener('DOMContentLoaded', function() {
    const usuario = sessionStorage.getItem('usuario');
    if (!usuario) {
        window.location.href = '/login';
    }
});

// Actualizar fecha
function updateDate() {
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    const today = new Date();
    const dateString = today.toLocaleDateString('es-CO', options);
    document.getElementById('currentDate').textContent = dateString;
}

updateDate();

function confirmarCerrarSesion(event) {
    event.preventDefault();
    
    if (confirm('¿Está seguro que desea cerrar sesión?')) {
        // Limpiar sessionStorage
        sessionStorage.clear();
        
        // Redirigir al logout
        window.location.href = '/logout';
    }
    
    return false;
}