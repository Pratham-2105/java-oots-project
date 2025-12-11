# Java Text Encryption Tool (Swing GUI)

This is a simple Java desktop application that demonstrates two types of text encryption:

1. **Caesar Cipher** — a basic character-shift encryption (for learning only)
2. **AES Encryption (CBC Mode)** — a more realistic demonstration of modern cryptography

The program has a small Swing GUI that allows the user to enter text, select the encryption method, and view the encrypted or decrypted output.

---

## Features

- Simple, easy-to-use interface (Java Swing)
- Caesar Encrypt & Decrypt
- AES Encrypt & Decrypt using:
  - Random AES-128 key
  - Random 16-byte IV
  - Base64 output
- Shows AES key used for encryption (for demo purposes)
- Works fully offline

---


## How to Run

1. Make sure you have **Java installed** (JDK 8 or above).
2. Compile both files:
   
javac GUI.java CryptoApp.java

Run the program:
java CryptoApp

How to Use

Enter any text into the input area.

Choose:

Caesar Cipher (requires you to enter a numeric key)

AES Encryption (key is generated automatically)

Click Encrypt to generate output.

Copy or save the encrypted text if needed.

For decryption:

Provide the encrypted text

For AES, also paste the Base64 key shown during encryption

Click Decrypt

The program handles the rest.
