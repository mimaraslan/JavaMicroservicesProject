-- PostgreSQL initialization script for microservices databases
-- This script creates all required databases for the microservices
-- Not: Bu script sadece veritabanı ilk kez başlatıldığında çalışır

-- Create AccountService database
CREATE DATABASE "Micro_AccountServiceDB";

-- Create LedgerService database
CREATE DATABASE "Micro_LedgerServiceDB";

-- Create FraudService database
CREATE DATABASE "Micro_FraudServiceDB";

-- Create NotificationService database
CREATE DATABASE "Micro_NotificationServiceDB";

-- Keycloak database is already created via POSTGRES_DB environment variable

