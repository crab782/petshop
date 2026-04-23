@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "CYAN=[96m"
set "RESET=[0m"
set "BOLD=[1m"

echo.
echo %CYAN%%BOLD%======================================================================%RESET%
echo %CYAN%%BOLD%            PetShop Test Runner                                    %RESET%
echo %CYAN%%BOLD%======================================================================%RESET%
echo.

echo %BLUE%[Checking Python]%RESET%
echo ----------------------------------------------------------------------
python --version >nul 2>&1
if errorlevel 1 (
    echo %RED%Python not installed or not in PATH%RESET%
    pause
    exit /b 1
)
for /f "tokens=2" %%i in ('python --version 2^>^&1') do set PYTHON_VERSION=%%i
echo %GREEN%OK%RESET% Python version: %PYTHON_VERSION%
echo.

echo %BLUE%[Checking Dependencies]%RESET%
echo ----------------------------------------------------------------------
python -c "import pytest" >nul 2>&1
if errorlevel 1 (
    echo %YELLOW%Installing pytest...%RESET%
    pip install pytest pytest-html pytest-cov allure-pytest requests python-dotenv pytest-xdist pytest-rerunfailures
) else (
    echo %GREEN%OK%RESET% pytest
)
python -c "import requests" >nul 2>&1
if errorlevel 1 (
    echo %RED%FAIL%RESET% requests not installed
) else (
    echo %GREEN%OK%RESET% requests
)
echo.

echo %BLUE%[Checking Backend Service]%RESET%
echo ----------------------------------------------------------------------
curl -s http://localhost:8080/api/health >nul 2>&1
if errorlevel 1 (
    echo %YELLOW%WARN%RESET% Backend service not running: http://localhost:8080
    echo Tests may fail without backend service
) else (
    echo %GREEN%OK%RESET% Backend service running: http://localhost:8080
)
echo.

echo %BLUE%[Running Tests]%RESET%
echo ----------------------------------------------------------------------

set TEST_ARGS=

:parse_args
if "%~1"=="" goto :execute
if /i "%~1"=="--all" set TEST_ARGS=%TEST_ARGS% --all
if /i "%~1"=="--auth" set TEST_ARGS=%TEST_ARGS% --auth
if /i "%~1"=="--appointment" set TEST_ARGS=%TEST_ARGS% --appointment
if /i "%~1"=="--product-order" set TEST_ARGS=%TEST_ARGS% --product-order
if /i "%~1"=="--isolation" set TEST_ARGS=%TEST_ARGS% --isolation
if /i "%~1"=="--performance" set TEST_ARGS=%TEST_ARGS% --performance
if /i "%~1"=="--smoke" set TEST_ARGS=%TEST_ARGS% --smoke
if /i "%~1"=="--regression" set TEST_ARGS=%TEST_ARGS% --regression
if /i "%~1"=="--user" set TEST_ARGS=%TEST_ARGS% --user
if /i "%~1"=="--merchant" set TEST_ARGS=%TEST_ARGS% --merchant
if /i "%~1"=="--admin" set TEST_ARGS=%TEST_ARGS% --admin
if /i "%~1"=="--security" set TEST_ARGS=%TEST_ARGS% --security
if /i "%~1"=="--report" set TEST_ARGS=%TEST_ARGS% --report
if /i "%~1"=="--coverage" set TEST_ARGS=%TEST_ARGS% --coverage
if /i "%~1"=="--allure" set TEST_ARGS=%TEST_ARGS% --allure
if /i "%~1"=="--cleanup" set TEST_ARGS=%TEST_ARGS% --cleanup
if /i "%~1"=="--help" goto :show_help
shift
goto :parse_args

:execute
echo Command: python run_tests.py%TEST_ARGS%
echo.
python run_tests.py%TEST_ARGS%
set TEST_EXIT_CODE=%ERRORLEVEL%
echo.

if %TEST_EXIT_CODE% equ 0 (
    echo.
    echo %GREEN%%BOLD%======================================================================%RESET%
    echo %GREEN%%BOLD%            Tests Completed - All Passed                            %RESET%
    echo %GREEN%%BOLD%======================================================================%RESET%
) else (
    echo.
    echo %RED%%BOLD%======================================================================%RESET%
    echo %RED%%BOLD%            Tests Completed - Some Failed                           %RESET%
    echo %RED%%BOLD%======================================================================%RESET%
)
echo.

if exist "tests\report\report.html" (
    echo %CYAN%HTML Report: tests\report\report.html%RESET%
    set /p OPEN_REPORT="Open HTML report? (y/n): "
    if /i "!OPEN_REPORT!"=="y" (
        start "" "tests\report\report.html"
    )
)

pause
exit /b %TEST_EXIT_CODE%

:show_help
echo.
echo %BOLD%Usage:%RESET% run_tests.bat [options]
echo.
echo %BOLD%Test Selection:%RESET%
echo   --all              Run all tests
echo   --auth             Run authentication tests
echo   --appointment      Run appointment tests
echo   --product-order    Run product order tests
echo   --isolation        Run data isolation tests
echo   --performance      Run performance tests
echo   --smoke            Run smoke tests
echo   --regression       Run regression tests
echo   --user             Run user-related tests
echo   --merchant         Run merchant-related tests
echo   --admin            Run admin-related tests
echo   --security         Run security tests
echo.
echo %BOLD%Report Options:%RESET%
echo   --report           Generate HTML report
echo   --coverage         Generate coverage report
echo   --allure           Generate Allure report
echo.
echo %BOLD%Other Options:%RESET%
echo   --cleanup          Cleanup test data after tests
echo   --help             Show this help message
echo.
echo %BOLD%Examples:%RESET%
echo   run_tests.bat --all                    Run all tests
echo   run_tests.bat --auth --report          Run auth tests with report
echo   run_tests.bat --all --coverage         Run all tests with coverage
echo.
pause
exit /b 0
