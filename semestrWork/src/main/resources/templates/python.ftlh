<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Запуск Python-кода</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/5.0.0-alpha2/css/bootstrap.min.css">
    <link rel="stylesheet" type="css" href="/static/css/styles.css">
</head>
<body>
<div class="container">
    <div class="form-block">
        <h2 class="text-center">Запуск Python-кода</h2>
        <form method="post" action="/run" id="pythonForm">
            <div class="mb-3">
                <label for="code" class="form-label">Введите ваш Python-код:</label>
                <textarea id="code" name="code" rows="10" class="form-control"
                          placeholder="Введите ваш Python-код здесь..."></textarea>
            </div>
            <button type="button" id="run" class="btn btn-primary">Запустить</button>

        </form>
        <div><br/></div>
        <button type="button" id="save" class="btn btn-secondary">Сохранить</button>
        <button type="button" id="history" class="btn btn-info">Посмотреть историю</button>
        <h2 class="mt-4">Результат:</h2>
        <pre id="result" class="border p-3 bg-light"></pre>
    </div>
</div>

<!-- Модальное окно для истории -->
<div class="modal fade" id="historyModal" tabindex="-1" aria-labelledby="historyModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="historyModalLabel">История сохраненных скриптов</h5>
                <button type="button" class="btn-close closer" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="historyContent">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary closer" data-bs-dismiss="modal">Закрыть</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-alpha2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Запуск кода
    document.getElementById('run').addEventListener('click', function () {
        const code = document.getElementById('code').value;
        fetch('/run', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({code})
        })
            .then(response => response.text())
            .then(result => {
                document.getElementById('result').textContent = result;
            })
            .catch(error => {
                document.getElementById('result').textContent = 'Ошибка: ' + error.message;
            });
    });

    // Сохранение кода
    document.getElementById('save').addEventListener('click', function () {
        const code = document.getElementById('code').value;
        fetch('/save', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({code})
        })
            .then(response => response.json())
            .then(data => {
                console.log(data)
                if (data.success) alert("Скрипт успешно сохранен!");
                else alert("Ошибка сохранения скрипта.");
            })
            .catch(error => {
                alert("Ошибка: " + error.message);
            });
    });

    // Просмотр истории
    document.getElementById('history').addEventListener('click', function () {
        fetch('/history')
            .then(response => response.json())
            .then(scripts => {
                const historyContent = document.getElementById('historyContent');
                historyContent.innerHTML = ''; // Очистить предыдущий контент

                scripts.forEach(script => {
                    console.log(script.content.json)
                    const scriptItem = document.createElement('div');
                    scriptItem.classList.add('script-item', 'mb-3');
                    scriptItem.innerHTML = `
                        <div class="d-flex justify-content-between">`
                        + script.fileName +
                        `<button class="btn btn-sm btn-outline-primary load-script">Загрузить</button>
                        </div
                        <div class="border p-2 bg-light mt-2">` + script.content + `</div>`;
                    // Добавляем обработчик для загрузки скрипта в текстовое поле
                    scriptItem.querySelector('.load-script').addEventListener('click', function () {
                        const fileId = script.id
                        console.log(fileId)
                        // Отправка POST-запроса для загрузки файла
                        fetch(`/download/` + fileId, {
                            method: 'GET'
                        })
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error("Ошибка загрузки файла.");
                                }
                                return response.blob();
                            })
                            .then(blob => {
                                // Создание временной ссылки для скачивания файла
                                const url = window.URL.createObjectURL(blob);
                                const a = document.createElement('a');
                                a.href = url;
                                a.download = script.fileName; // Устанавливаем имя файла
                                document.body.appendChild(a);
                                a.click();
                                a.remove();
                                window.URL.revokeObjectURL(url);
                            })
                            .catch(error => {
                                alert("Ошибка при скачивании файла: " + error.message);
                            });
                    });
                    historyContent.appendChild(scriptItem);
                });

                // Показать модальное окно
                new bootstrap.Modal(document.getElementById('historyModal')).show();
            })
            .catch(error => {
                alert("Ошибка загрузки истории: " + error.message);
            });

        const btnsClose = document.querySelectorAll(".closer")

        btnsClose.forEach(btn =>  btn.addEventListener('click', () => {
            (document.getElementById('historyModal')).classList.remove("show")
        }));
    });
</script>
</body>
</html>
