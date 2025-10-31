document.addEventListener('DOMContentLoaded', () => {

    const itemList = document.getElementById('item-list');

    const addModal = document.getElementById('add-item-modal');
    const editModal = document.getElementById('edit-item-modal');
    const showAddModalBtn = document.getElementById('show-add-modal-btn');
    const closeButtons = document.querySelectorAll('.btn-close');

    const closeAllModals = () => {
        addModal.style.display = 'none';
        editModal.style.display = 'none';
    };

    showAddModalBtn.addEventListener('click', () => {
        addModal.style.display = 'flex';
    });

    closeButtons.forEach(btn => {
        btn.addEventListener('click', closeAllModals);
    });


    const addForm = document.getElementById('add-item-form');
    addForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(addForm);
        const itemData = Object.fromEntries(formData.entries());

        try {
            const response = await fetch('/api/v1/items', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(itemData)
            });

            const result = await response.json();

            if (result.success) {
                
                location.reload();

                // (Advanced way: You would build the HTML card here in JS

            } else {
                alert('Error: ' + result.message);
            }
        } catch (err) {
            alert('An error occurred: ' + err.message);
        }
    });


    const editForm = document.getElementById('edit-item-form');

    itemList.addEventListener('click', async (e) => {
        if (e.target.classList.contains('btn-edit')) {
            const id = e.target.dataset.id;

            const response = await fetch(`/api/v1/items/${id}`);
            const result = await response.json();

            if (result.success) {
                const item = result.data;
                editForm.querySelector('[name="id"]').value = item.id;
                editForm.querySelector('[name="name"]').value = item.name;
                editForm.querySelector('[name="description"]').value = item.description;
                editForm.querySelector('[name="price"]').value = item.price;
                editForm.querySelector('[name="inventory"]').value = item.inventory;

                editModal.style.display = 'flex';
            }
        }
    });

    editForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(editForm);
        const itemData = Object.fromEntries(formData.entries());
        const id = itemData.id;

        try {
            const response = await fetch(`/api/v1/items/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(itemData)
            });

            const result = await response.json();

            if (result.success) {
                // Easiest way to see the change: reload.
                location.reload();
            } else {
                alert('Error: ' + result.message);
            }
        } catch (err) {
            alert('An error occurred: ' + err.message);
        }
    });
});