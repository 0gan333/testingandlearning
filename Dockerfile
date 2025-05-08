# Use an official Maven image with Eclipse Temurin JDK 17
FROM maven:3.8.7-eclipse-temurin-17

# Install required packages for Chrome and XVFB
RUN apt-get update && \
    apt-get install -y wget gnupg2 unzip curl xvfb fonts-liberation libappindicator3-1 libasound2 libatk-bridge2.0-0 libatk1.0-0 libcups2 libdbus-1-3 libgdk-pixbuf2.0-0 libnspr4 libnss3 libxcomposite1 libxdamage1 libxrandr2 xdg-utils --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# Add Google's signing key and Chrome repository, then install Chrome
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# Copy project into the container
COPY . /app
WORKDIR /app

# Add and set permissions for the entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN sed -i 's/\r$//' /entrypoint.sh && chmod +x /entrypoint.sh

# Set the entrypoint
ENTRYPOINT ["/entrypoint.sh"]
