// Oculta el toast automaticamente despues de 4 segundos
setTimeout(() => {
    document.querySelectorAll('.toast').forEach(t => t.classList.remove('show'));
}, 4000);

// Inserta informacion en el modal de confirmacion segun el registro seleccionado
document.addEventListener('DOMContentLoaded', function () {
    const confirmModal = document.getElementById('confirmModal');
    if (confirmModal) {
        confirmModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const idInput = document.getElementById('modalId');
            const descSpan = document.getElementById('modalDescripcion');
            if (idInput) idInput.value = button.getAttribute('data-bs-id');
            if (descSpan) descSpan.textContent = button.getAttribute('data-bs-descripcion');
        });
    }
});

// Preview de imagen: toma la URL del input texto y la muestra
function mostrarImagen(input) {
    var url = input.value;
    var img = document.getElementById('blah');
    if (url && img) {
        img.src = url;
        img.style.height = '200px';
        img.style.display = 'block';
    }
}
