#                                                                        Tour into this application 
                                                                       
###                                                                      How to use and technologies                                                         


#### ✏️ Overview:
###### Application made during the recruitment process, the idea is to create an order management API.


#                 💻 Technologies:

#### Java 21
#### Spring boot
#### JPA / Hibernate
#### Postgres
#### jUnit 5
#### Mockito
#### Maven
#### Lombok
#### Log4j



## How to run

#### Clone the Repository: Use git clone <repository-url>
#### Navigate to Project Directory: Use cd project-directory
#### Build Docker Image: Use docker-compose build
#### Start Docker Containers: Use docker-compose up
#### Access Application: Open http://localhost:8080 in your browser.

## Initialize the Data

#### Create Users: POST to /users with the user details in JSON format.
#### Create Items: POST to /items with the item details in JSON format.
#### Create Stock Movements: POST to /stock-movements with the stock movement details in JSON format.
#### Place and Manage Orders

#### Create an Order: POST to /orders with the order details in JSON format.
#### Check Order Status: GET to /orders/{orderId} to check the status of the specific order.
#### List All Orders: GET to /orders to list all orders.
#### Update an Order: PUT to /orders/{orderId} with the updated order details in JSON format.
#### Delete an Order: DELETE to /orders/{orderId} to delete the specific order.

# Conclusions
### I learned a lot from this project, it was developed in just a few hours, so there's definitely room to improve.

