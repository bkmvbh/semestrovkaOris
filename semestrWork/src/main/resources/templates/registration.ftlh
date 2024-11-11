<html>
<head>
    <title>Регистрация</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/5.0.0-alpha2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/static/css/registr.css">
</head>
<body>
<div class="container">
    <div class="form-block">
        <form action="/registration/new" method="post">

            <h2 style="text-align: center;">Регистрация</h2>
            <h4 style="text-align: center;color: red; margin-top: 40px">${error!""}</h4>

            <div class="mb-3">
                <label for="username" class="form-label">Ваше имя:</label>
                <input type="text" id="username" name="fullname" required class="form-control" placeholder="Введите ваше имя">
            </div>

            <div class="mb-3">
                <label for="userlogin" class="form-label">Придумайте логин:</label>
                <input type="text" id="userlogin" name="username" required class="form-control" placeholder="Придумайте логин" oninput="validateLogin(this)">
            </div>

            <div class="mb-3">
                <label for="useremail" class="form-label">Почта:</label>
                <input type="text" id="useremail" name="useremail" required class="form-control" placeholder="Введите вашу почту" oninput="validateEmail(this)">
            </div>

            <div class="mb-3">
                <label for="password" class="form-label">Придумайте пароль:</label>
                <input type="password" id="password" name="password" required class="form-control" placeholder="Введите пароль" oninput="validatePassword(this)">
            </div>

            <div class="mb-3">
                <label for="repeat_password" class="form-label">Повторите пароль:</label>
                <input type="password" id="repeat_password" name="repeatPassword" required class="form-control" placeholder="Введите пароль" >
            </div>

            <input type="submit" value="Зарегистрироваться" class="btn btn-primary">
        </form>
        <div style="text-align: center; margin-top: 20px;">
            <a href="/login">Уже зарегистрированы? Войти</a>
        </div>
    </div>

</div>
</body>
</html>
