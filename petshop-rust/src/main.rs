use actix_web::{web, App, HttpServer};
use log::info;
use utoipa_swagger_ui::SwaggerUi;
use petshop_rust::api;
use petshop_rust::config::Config;
use petshop_rust::database;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    env_logger::init();
    dotenv::dotenv().ok();

    let config = Config::load();

    database::init_pool();

    let host = "0.0.0.0";
    let port = config.app.port;

    info!("Starting server at http://{}:{}", host, port);

    HttpServer::new(move || {
        let api_doc = petshop_rust::api::ApiDoc::openapi();
        App::new()
            .app_data(web::Data::new(database::get_pool()))
            .configure(api::auth::configure)
            .configure(api::user::configure)
            .configure(api::merchant::configure)
            .configure(api::admin::configure)
            .configure(api::public::configure)
            .configure(api::product::configure)
            .configure(api::service::configure)
            .configure(api::cart::configure)
            .configure(api::order::configure)
            .configure(api::appointment::configure)
            .configure(api::review::configure)
            .configure(api::announcement::configure)
            .configure(api::search::configure)
            .service(
                SwaggerUi::new("/swagger-ui/{_:.*}")
                    .url("/api-docs/openapi.json", api_doc)
            )
    })
    .bind((host, port))?
    .run()
    .await
}