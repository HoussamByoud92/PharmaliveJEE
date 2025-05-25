# 💊 PHARMALIVE

**PHARMALIVE** is a full-featured Java EE web application to manage pharmaceutical stock and inventory. It provides an intuitive interface for pharmacists and administrators, offering real-time alerts, user management, medicine stock tracking, and PDF/CSV exports.

> **Languages :** 🇬🇧 English | 🇫🇷 Français *(voir ci-dessous)*

---

## 🌟 Features

- 🔐 Secure login system (Admin & User roles)
- 💊 Medicine inventory management
- 📉 Expiration date alerts
- 📦 Low stock notifications
- 🔍 Dashboard with filters/search
- 📁 Export CSV & PDF invoices
- 🔔 Notification-ready architecture
- 🧑‍🤝‍🧑 Multi-user support

---

## 🛠️ Tech Stack

| Layer       | Technology              |
|-------------|--------------------------|
| Backend     | Java EE (Servlets, JSP) |
| Frontend    | JSP, HTML, CSS, JS      |
| Database    | MySQL                   |
| Server      | Apache Tomcat 10.1.41   |
| Build Tool  | Maven                   |
| IDE         | IntelliJ IDEA           |
| Versioning  | Git + Azure DevOps      |
| Deployment  | Docker, Azure Pipelines |

---

## ⚙️ Project Setup

### ✅ Prerequisites

- Java JDK 24
- Apache Tomcat 10.1.41
- Maven 3.x
- MySQL Server
- IntelliJ IDEA (recommended)

---

### 🔁 Clone the Repository

```bash
git clone https://github.com/HoussamByoud92/PharmaliveJEE.git
cd PharmaliveJEE
```

### 🗄️ Create MySQL Database

```sql
CREATE DATABASE pharmalive;
```

(Optional) Import schema from pharmalive_db.sql if available:

```bash
mysql -u root -p pharmalive < pharmalive_db.sql
```

### 🛠️ Configure Database Connection

Open `src/main/java/com/live/pharmaliv/dao/DatabaseUtil.java` and update:

```java
private static final String URL = "jdbc:mysql://localhost:3306/pharmalive";
private static final String USER = "your_mysql_username";
private static final String PASSWORD = "your_mysql_password";
```

### 🧱 Build the Project

Use Maven to compile the project:

```bash
mvn clean install
```

This will create a WAR file at:

```bash
/target/pharmalive-1.0-SNAPSHOT.war
```

### 🔧 Configure Tomcat in IntelliJ

1. Go to **Run > Edit Configurations**
2. Click **+ → Tomcat Server > Local**
3. Name it: **Tomcat 10**
4. Click **Configure** and locate your Tomcat installation folder
5. In the **Deployment** tab:
   - Click **+ → Artifact**
   - Select **pharmalive:war exploded**
   - Set **Application context** to:
     ```bash
     /pharmalive
     ```
6. In **Before Launch**, ensure "Build" is selected
7. Click **Apply** and **OK**

### ▶️ Run the App

1. Click the green **Run (▶️)** button in IntelliJ.
2. Open browser at:
   ```bash
   http://localhost:8080/pharmalive/login
   ```

---

## 🐳 Docker Deployment

### 🔄 Build Docker Image

Build the Docker image using the provided Dockerfile:

```bash
docker build -t pharmalive:latest .
```

### 🚀 Run Docker Container

Run the container with default settings:

```bash
docker run -d -p 8080:8080 --name pharmalive-app pharmalive:latest
```

Access the application at:
```bash
http://localhost:8080/
```

### 🔧 Environment Variables

The Docker container supports the following environment variables for database configuration:

| Variable     | Default     | Description                  |
|--------------|-------------|------------------------------|
| DB_HOST      | mysql       | Database hostname/IP         |
| DB_PORT      | 3306        | Database port                |
| DB_NAME      | pharmalive  | Database name                |
| DB_USER      | root        | Database username            |
| DB_PASSWORD  | password    | Database password            |

Example with custom database settings:

```bash
docker run -d -p 8080:8080 \
  -e DB_HOST=db.example.com \
  -e DB_USER=pharma_user \
  -e DB_PASSWORD=secure_password \
  --name pharmalive-app \
  pharmalive:latest
```

### 🔗 Using with Docker Compose

Create a `docker-compose.yml` file for easy deployment with MySQL:

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=db
      - DB_USER=pharmalive
      - DB_PASSWORD=pharmalive_password
      - DB_NAME=pharmalive
    depends_on:
      - db

  db:
    image: mysql:8.0
    environment:
      - MYSQL_DATABASE=pharmalive
      - MYSQL_USER=pharmalive
      - MYSQL_PASSWORD=pharmalive_password
      - MYSQL_ROOT_PASSWORD=root_password
    volumes:
      - ./pharmaliv/DB/schema.sql:/docker-entrypoint-initdb.d/schema.sql
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

Run with Docker Compose:

```bash
docker-compose up -d
```

---

## 🧪 Default Login Credentials (for testing)

```
Admin:
Username: admin
Password: admin123

```

---

## 📂 Directory Structure

```bash
pharmaliveJEE/
├── src/
│   ├── controller/       # Servlets
│   ├── dao/              # Database Access
│   ├── model/            # JavaBeans
│   ├── utils/            # DB connection helpers
│   └── webapp/
│       ├── jsp/          # JSP views
│       └── WEB-INF/      # web.xml config
├── pom.xml
└── target/
```

---

## 👥 Contributors

Project developed collaboratively by:

- 👨‍💻 **Sohaib Laarichi** – Lead Developer
- 👨‍💻 **Houssam Byoud**
- 👩‍💻 **Nada Jamim**
- 👩‍💻 **Oumaima Aarab**
- 👨‍💻 **Osama Mansori**
- 👩‍💻 **Inasse Telki**

*Managed and versioned using Azure DevOps.*

---

## 🇫🇷 PHARMALIVE – Application Web de Gestion de Stock Pharmaceutique

**PHARMALIVE** est une application Web développée en Java EE (JSP/Servlets) pour la gestion de stock en pharmacie. Elle offre une interface intuitive pour suivre les produits, recevoir des alertes de péremption ou de stock faible, exporter des données, et bien plus.

### 🔹 Fonctionnalités

- Connexion sécurisée avec rôles
- Tableau de bord interactif
- Gestion des médicaments
- Alertes de stock faible et de péremption
- Export PDF
- Gestion multi-utilisateur

### 🔹 Prérequis

- JDK 24
- Apache Tomcat 10.1.41
- MySQL
- Maven
- IntelliJ IDEA

### 🔹 Installation

1. **Cloner le dépôt :**
   ```bash
   git clone https://github.com/HoussamByoud92/PharmaliveJEE
   ```

2. **Créer la base de données MySQL :**
   ```sql
   CREATE DATABASE pharmalive;
   ```

3. **Configurer DBUtil.java** avec vos identifiants MySQL

4. **Compiler avec Maven :**
   ```bash
   mvn clean install
   ```

5. **Déployer le .war** sur Tomcat via IntelliJ

6. **Accéder à l'application :**
   ```bash
   http://localhost:8080/pharmalive/login
   ```

### 📎 Lien du Dépôt

🔗 [GitHub – PharmaliveJEE](https://github.com/HoussamByoud92/PharmaliveJEE)

### 🙌 Contributions Bienvenues

N'hésitez pas à proposer des améliorations via pull request.
```
