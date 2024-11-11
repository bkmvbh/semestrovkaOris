<html>
<head>
    <title>Авторизация</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/5.0.0-alpha2/css/bootstrap.min.css">
    <link rel="stylesheet" type="css" href="/static/css/registr.css">
</head>
<body>
<h3 style="text-align: center;color: red; margin-top: 40px">${message!""}</h3>
<div class="container">
    <div class="form-block">
        <form action="/usercheck" method="post">

            <h2 style="text-align: center;">Пожалуйста, авторизуйтесь</h2>

            <div class="mb-3">
                <label for="username" class="form-label">Введите логин:</label>
                <input type="text" id="username" name="username" required class="form-control" placeholder="Введите логин">
            </div>

            <div class="mb-3">
                <label for="password" class="form-label">Введите пароль:</label>
                <input type="password" id="password" name="password" required class="form-control" placeholder="Введите пароль">
            </div>

            <input type="submit" value="Войти" class="btn btn-primary">
        </form>
        <div style="text-align: center; margin-top: 20px;">
            <a href="/registration">Ещё не зарегистрированы? Зарегистрироваться</a>
        </div>
    </div>

</div>
</body>
</html>
