---
openapi: 3.0.1
info:
  title: my web
  description: good
  version: 1.0.0
servers:
  - url: http://localhost:8080
    description: Generated server url
paths:
  "/yongho":
    get:
      tags:
        - test controller
      summary: testing!
      operationId: ex02
      parameters:
        - name: arg01
          in: query
          required: true
          schema:
            type: string
        - name: arg02
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: OK
          content:
            "*/*":
              schema:
                type: string
  "/ex01":
    get:
      tags:
        - test controller
      summary: this api ddis awesome
      operationId: ex01
      parameters:
        - name: arg01
          in: query
          required: true
          schema:
            type: string
        - name: arg02
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: OK
          content:
            "*/*":
              schema:
                type: string
components: {}
