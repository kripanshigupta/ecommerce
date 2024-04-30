package org.example.ecommerce;

import com.mysql.cj.protocol.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.SQLException;

public class UserInterface {

    GridPane loginPage;

    HBox headerBar;

    HBox footerBar;

    VBox body;

    Customer loggedInCustomer;

    Button signInButton;

    Label welcomeLabel;

    ProductList productList=new ProductList();

    VBox productPage;

    ObservableList<Product> itemsInCart= FXCollections.observableArrayList();

    Button placeOrderButton=new Button("Place Order");

    BorderPane createContent(){
        BorderPane root =new BorderPane();
        root.setPrefSize(800,600);
        //root.getChildren().add(loginPage);//methods to add nodes as children to pane
        root.setTop(headerBar);
        //root.setCenter(loginPage);
        body= new VBox();
        body.setPadding(new Insets(10));
        body.setAlignment(Pos.CENTER);
        root.setCenter(body);
        productPage=productList.getAllProducts();
        body.getChildren().add(productPage);
        root.setBottom(footerBar);
        return root;
    }

    public UserInterface(){
        createLoginPage();
        createHeaderBar();
        createFooterBar();
    }

    private void createLoginPage(){
        Text userNameText =new Text("User Name");
        Text passwordText =new Text("Password");

        TextField userName =new TextField("angad@gmail.com");
        userName.setPromptText("Type your username here.");
        PasswordField password =new PasswordField();
        password.setText("abc123");
        password.setPromptText("Type your password here.");

        Button loginButton=new Button("Login");
        Label messageLabel=new Label("");

        loginPage=new GridPane();
        //loginPage.setStyle("-fx-background-color:grey");
        loginPage.setAlignment(Pos.CENTER);
        loginPage.setHgap(10);
        loginPage.setVgap(10);
        loginPage.add(userNameText,0,0);
        loginPage.add(userName,1,0);
        loginPage.add(passwordText,0,1);
        loginPage.add(password,1,1);
        loginPage.add(messageLabel,0,2);
        loginPage.add(loginButton,1,2);



        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String name=userName.getText();
                String pass=password.getText();
                Login login=new Login();
                try {
                    loggedInCustomer=login.customerLogin(name,pass);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (loggedInCustomer!=null){
                    messageLabel.setText("Welcome : "+loggedInCustomer.getName());
                    welcomeLabel.setText("Welcome-"+loggedInCustomer.getName());
                    headerBar.getChildren().add(welcomeLabel);
                    body.getChildren().clear();
                    body.getChildren().add(productPage);
                }
                else{
                    messageLabel.setText("Login Failed!");
                }
            }
        });
    }

    private void createHeaderBar(){
        Button homeButton=new Button();
        Image image=new Image("C:\\Users\\hp\\OneDrive\\Documents\\NetBeansProjects\\ECommerce\\src\\download.png");
        ImageView imageView=new ImageView();
        imageView.setImage(image);
        imageView.setFitHeight(30);;
        imageView.setFitWidth(60);
        homeButton.setGraphic(imageView);

        TextField searchBar=new TextField();
        searchBar.setPromptText("Search here");
        searchBar.setPrefWidth(280);

        Button searchButton=new Button("Search");
        signInButton=new Button("Sign In");
        welcomeLabel=new Label();

        Button cartButton=new Button("Cart");
        //Button orderButton=new Button("Orders");

        headerBar=new HBox();
        headerBar.setSpacing(10);
        //headerBar.setStyle("-fx-background-color:grey");
        headerBar.setAlignment(Pos.CENTER);
        headerBar.setPadding(new Insets(10));
        headerBar.getChildren().addAll(homeButton,searchBar,searchButton,signInButton,cartButton);

        signInButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                body.getChildren().clear();//removes everything
                body.getChildren().add(loginPage);//put login page
                headerBar.getChildren().remove(signInButton);
            }
        });

        cartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                body.getChildren().clear();
                VBox prodPage=productList.getProductsInCart(itemsInCart);
                prodPage.getChildren().add(placeOrderButton);
                body.getChildren().add(prodPage);
                prodPage.setAlignment(Pos.CENTER);
                prodPage.setSpacing(10);
                footerBar.setVisible(false);//for all cases, needs to be handled
            }
        });


        placeOrderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(itemsInCart==null){
                    showDialog("Please add some products in the cart to place order!");
                    return;
                }
                if(loggedInCustomer==null){
                    showDialog("Please login first to place order!");
                    return;
                }
                int count=Order.placeMultipleOrder(loggedInCustomer,itemsInCart);
                if (count!=0){
                    showDialog("Order for "+count+" products placed successfully!");
                }
                else {
                    showDialog("Order failed!");
                }
            }
        });

        homeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                body.getChildren().clear();
                body.getChildren().add(productPage);
                footerBar.setVisible(true);
                if (loggedInCustomer==null && headerBar.getChildren().indexOf(signInButton)==-1){
                    headerBar.getChildren().add(signInButton);
                }
            }
        });

    }

    private void createFooterBar(){
        Button buyNowButton=new Button("Buy Now");
        Button addToCartButton=new Button("Add to Cart");

        footerBar=new HBox();
        footerBar.setSpacing(10);
        footerBar.setAlignment(Pos.CENTER);
        footerBar.setPadding(new Insets(10));
        footerBar.getChildren().addAll(buyNowButton,addToCartButton);

        buyNowButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Product product=productList.getSelectedProduct();
                if(product==null){
                    //please select a product first to place order
                    showDialog("Please select a product first to place order!");
                    return;
                }
                if(loggedInCustomer==null){
                    showDialog("Please login first to place order!");
                    return;
                }
                boolean status=Order.placeOrder(loggedInCustomer,product);
                if (status){
                    showDialog("Order placed successfully!");
                }
                else {
                    showDialog("Order failed!");
                }
            }
        });

        addToCartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Product product=productList.getSelectedProduct();
                if(product==null){
                    showDialog("Please select a product first to add it to cart!");
                    return;
                }
                itemsInCart.add(product);
                showDialog("Item added to the cart.");
            }
        });

    }

    private void showDialog(String message){
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setTitle("Message");
        alert.showAndWait();
    }

}
