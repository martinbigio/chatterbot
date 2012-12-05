package ar.edu.itba.tpf.chatterbot.db;

import java.util.GregorianCalendar;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;

import ar.edu.itba.tpf.chatterbot.model.Chat;
import ar.edu.itba.tpf.chatterbot.model.ChatEntry;
import ar.edu.itba.tpf.chatterbot.model.ErrorLog;
import ar.edu.itba.tpf.chatterbot.model.GlobalNode;
import ar.edu.itba.tpf.chatterbot.model.InternalNode;
import ar.edu.itba.tpf.chatterbot.model.Role;
import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.model.User;
import ar.edu.itba.tpf.chatterbot.model.WebServiceAction;
import ar.edu.itba.tpf.chatterbot.service.ChatterbotService;
import ar.edu.itba.tpf.chatterbot.service.LoggingService;
import ar.edu.itba.tpf.chatterbot.service.ServerService;
import ar.edu.itba.tpf.chatterbot.service.UserService;

public class StartUpDatabase {

    private ApplicationContext ctx;

    public StartUpDatabase() {
        ctx = new FileSystemXmlApplicationContext(new String[] { "../impl/src/main/resources/impl-data.xml",
                "../impl/src/main/resources/impl-beans.xml", "src/main/webapp/WEB-INF/springContext-security.xml",
                "src/main/webapp/WEB-INF/springContext-beans.xml" });

    }

    public void createSchemaFile(String outputFile) {
        AnnotationSessionFactoryBean s = (AnnotationSessionFactoryBean) ctx.getBean("&sessionFactory");

        Configuration cfg = s.getConfiguration();
        SchemaExport schemaExport = new SchemaExport(cfg);

        schemaExport.setOutputFile(outputFile);
        schemaExport.setDelimiter(";");
        schemaExport.create(true, false);
    }

    // public void prueba() {
    // ChatterbotService chatterbotService = (ChatterbotService) ctx.getBean("chatterbotService");
    //
    // GlobalNode globalNode = chatterbotService.getGlobalNodes().iterator().next();
    //
    // System.out.println("GlobalNode: " + globalNode);
    // System.out.println("Cantidad de acciones: " + chatterbotService.getAllActions().size());
    //
    // globalNode.setAction(chatterbotService.getAllActions().iterator().next());
    // System.out.println("Persistiendo globalNode");
    // chatterbotService.persistGlobalNode(globalNode);
    //
    // System.out.println("Cantidad de acciones: " + chatterbotService.getAllActions().size());
    // }

    public void loadUsers() {
        UserService userService = (UserService) ctx.getBean("userService");
        ShaPasswordEncoder shaPasswordEncoder = (ShaPasswordEncoder) ctx.getBean("shaPasswordEncoder");

        Role roleAdmin = new Role("ROLE_ADMIN", "Administrador");
        userService.persistRole(roleAdmin);

        User userAdmin = new User("admin", shaPasswordEncoder.encodePassword("admin", null), "Administrador",
                "Administrador", "admin@chatterbot.com");
        userAdmin.addRole(roleAdmin);
        userService.persistUser(userAdmin);

        Role roleAnalyst = new Role("ROLE_USER", "Analista");
        userService.persistRole(roleAnalyst);

        User userAnalyst = new User("user", shaPasswordEncoder.encodePassword("user", null), "Analyst", "Analyst",
                "user@chatterbot.com");
        userAnalyst.addRole(roleAnalyst);
        userService.persistUser(userAnalyst);
    }

    public void loadServers() {
        ServerService serverService = (ServerService) ctx.getBean("serverService");

        serverService.persistServer(new Server("fake-server1", "srv1.mycompany.com", 3000, 10000, 0.8F, false));
        serverService.persistServer(new Server("fake-server2", "srv2.mycompany.com", 3001, 15000, 0.7F, false));
        serverService.persistServer(new Server("fake-server3", "srv3.mycompany.com", 3000, 20000, 0.95F, false));
        serverService.persistServer(new Server("fake-server4", "srv4.mycompany.com", 3002, 10000, 0.5F, false));
        serverService.persistServer(new Server("fake-server5", "srv5.mycompany.com", 3000, 8000, 0.75F, false));
    }

    public void loadNodes() {
        ChatterbotService chatterbotService = (ChatterbotService) ctx.getBean("chatterbotService");

        InternalNode rootNode = new InternalNode(null, "", "Nodo raíz",
                "Muchas gracias por su consulta. Si tiene otra duda pregunte.", null);

        InternalNode saldoNode = new InternalNode(null, "consultar saldo verificar ultimo estado cuenta",
                "Consultar el saldo de la cuenta", "Por favor ingrese su número de cuenta (\\d{10}).", null);

        InternalNode obtenerNroCuentaNode = new InternalNode(chatterbotService
                .getActionByDescription("Validar número de cuenta"), "", "Validar número de cuenta",
                "Por favor ingrese su  DNI.", saldoNode);

        InternalNode obtenerDNI = new InternalNode(chatterbotService.getActionByDescription("Consultar saldo"), "",
                "Obtiene el DNI del usuario y muestra el saldo de la cuenta",
                "El saldo de su cuenta N° ${numeroCuenta} es ${saldoCuenta}.", obtenerNroCuentaNode);

        obtenerNroCuentaNode.addChild(obtenerDNI);
        saldoNode.addChild(obtenerNroCuentaNode);
        rootNode.addChild(saldoNode);

        InternalNode tarjetaCreditoNode = new InternalNode(null, "consultar vencimiento tarjeta credito crédito",
                "Consultar fecha de pago de la tarjeta de credito",
                "Por favor ingrese su número de tarjeta de crédito (\\d{10}).", null);

        InternalNode obtenerNroTarjetaNode = new InternalNode(chatterbotService
                .getActionByDescription("Validar número de tarjeta de crédito"), "", "Validar número de tarjeta",
                "Por favor ingrese su el vencimiento de su tarjeta (MM/AAAA).", tarjetaCreditoNode);

        InternalNode obtenerVencimientoTarjeta = new InternalNode(
                chatterbotService.getActionByDescription("Consultar vencimiento de tarjeta de crédito"),
                "",
                "Obtiene el vencimiento de la tarjeta de crédito y consultar la fecha de pago",
                "El pago de su tarjeta de crédito número ${numeroTarjetaCredito}(cuyo vencimiento es ${vencimientoTarjetaCredito}) es ${fechaPagoTarjetaCredito}",
                obtenerNroTarjetaNode);

        tarjetaCreditoNode.addChild(obtenerNroTarjetaNode);
        obtenerNroTarjetaNode.addChild(obtenerVencimientoTarjeta);
        rootNode.addChild(tarjetaCreditoNode);

        InternalNode horarioNode = new InternalNode(null, "horario", "Consultar horario de atención",
                "Por favor ingrese su sucursal.", null);
        horarioNode.addChild(new InternalNode(null, "palermo", "Sucursal Palermo",
                "El horario de atención de la Sucursal Palermo es de 8:00 a 13:00", null));
        horarioNode.addChild(new InternalNode(null, "belgrano", "Sucursal Belgrano",
                "El horario de atención de la Sucursal Belgrano es de 8:00 a 13:00", null));
        horarioNode.addChild(new InternalNode(null, "caballito", "Sucursal Caballito",
                "El horario de atención de la Sucursal Caballito es de 8:00 a 13:00", null));
        rootNode.addChild(horarioNode);

        chatterbotService.persistTreeNode(rootNode);
    }

    public void loadGlobalNodes() {
        ChatterbotService chatterbotService = (ChatterbotService) ctx.getBean("chatterbotService");

        chatterbotService.persistGlobalNode(new GlobalNode(null, "hola", "saludo1", "Hola, en qué lo puedo ayudar?"));
        chatterbotService.persistGlobalNode(new GlobalNode(null, "buen dia", "saludo2",
                "Muy buenos días, en qué lo puedo ayudar?"));
        chatterbotService.persistGlobalNode(new GlobalNode(null, "gracias", "agradecimiento", "De nada."));
        chatterbotService.persistGlobalNode(new GlobalNode(null, "chau", "terminar conversacion1", "Chau"));
        chatterbotService.persistGlobalNode(new GlobalNode(null, "hasta luego", "terminar conversacion2", "Adios"));
        chatterbotService.persistGlobalNode(new GlobalNode(null, "adios", "terminar conversacion3", "Adios"));
        chatterbotService
                .persistGlobalNode(new GlobalNode(null, "nos vemos", "terminar conversacion4", "Hasta pronto"));
        chatterbotService.persistGlobalNode(new GlobalNode(null, "suerte", "terminar conversacion5", "Igualmente"));
    }

    public void loadChats() {

        LoggingService loggingService = (LoggingService) ctx.getBean("loggingService");
        ServerService serverService = (ServerService) ctx.getBean("serverService");

        Server server = new Server("server-test1", "server-test1.localhost.com", 1000, 10002, 0.5F, false);
        Server server2 = new Server("server-test2", "server-test2.localhost.com", 1000, 10002, 0.5F, false);
        serverService.persistServer(server);

        Chat chat = new Chat("jose@mail.com", server);

        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 30, 0).getTime(),
                "Hola, como estas?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 30, 20).getTime(), "Hola, bien.",
                false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 31, 10).getTime(),
                "Esta abierta la sucursal?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 31, 15).getTime(),
                "El horario de atención es de 8:00 a 16:00 hs.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 33, 0).getTime(),
                "Realizan cambio de dólares?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 33, 10).getTime(),
                "Se realizacn cambios de dólares. El cambio actual es de " + "3.14 U$S.", false));
        chat.setSuccessful(true);
        chat.setFinalNode("cambio dolares");

        loggingService.persistChat(chat);

        chat = new Chat("otro@mail.com", server2);

        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 15, 30, 0).getTime(), "Hola.", true));
        chat
                .addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 15, 30, 10).getTime(), "Hola.",
                        false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 15, 30, 15).getTime(),
                "Cual es la tasa de un plazo fijo a 30 dias?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 15, 30, 30).getTime(),
                "El banco ofrece un plazo fijo a tasa nominal 3.4%.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 15, 30, 35).getTime(),
                "Cuales son los requisitos?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 15, 30, 40).getTime(),
                "Los requisitos son ser mayor de edad (21 años) y tener DNI." + "3.14 U$S.", false));
        chat.setSuccessful(true);
        chat.setFinalNode("requisitos plazo fijo");

        chat = new Chat("otro@mail.com", server2);
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 17, 14, 00, 0).getTime(),
                "Hola, como estas?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 17, 14, 00, 30).getTime(), "Hola, bien.",
                false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 17, 14, 02, 10).getTime(),
                "Esta abierta la sucursal?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 17, 14, 02, 45).getTime(),
                "El horario de atención es de 8:00 a 16:00 hs.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 17, 14, 03, 10).getTime(),
                "Realizan cambio de dólares?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 17, 14, 03, 20).getTime(),
                "Se realizacn cambios de dólares. El cambio actual es de " + "3.14 U$S.", false));
        chat.setSuccessful(true);
        chat.setFinalNode("cambio dolares");
        loggingService.persistChat(chat);

        chat = new Chat("otro@mail.com", server2);

        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 20, 30, 0).getTime(), "Hola.", true));
        chat
                .addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 20, 30, 30).getTime(), "Hola.",
                        false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 20, 30, 50).getTime(),
                "Cual es la tasa de un plazo fijo a 30 dias?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 20, 31, 5).getTime(),
                "El banco ofrece un plazo fijo a tasa nominal 3.4%.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 20, 31, 20).getTime(),
                "Cuales son los requisitos?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 20, 31, 50).getTime(),
                "Los requisitos son ser mayor de edad (21 años) y tener DNI." + "3.14 U$S.", false));
        chat.setSuccessful(true);
        chat.setFinalNode("requisitos plazo fijo");
        loggingService.persistChat(chat);

        chat = new Chat("juan@hotmail.com", server);

        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 30, 0).getTime(), "Hola.", true));
        chat
                .addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 30, 10).getTime(), "Hola.",
                        false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 30, 20).getTime(),
                "Cual es el precio de un perro de raza caniche?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 30, 30).getTime(),
                "No entiendo su pregunta, por favor reformulela.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 30, 40).getTime(),
                "Claro, necesito comprar un perro.", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 16, 14, 30, 50).getTime(),
                "No entiendo su pregunta, por favor reformulela." + "3.14 U$S.", false));
        chat.setSuccessful(false);

        chat = new Chat("gustavo@yahoo.com.ar", server);

        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 30, 10).getTime(),
                "Necesito saber si ofrecer venta de opciones?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 30, 20).getTime(),
                "Hola, en este momento no estamos ofreciendo ese servicio.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 31, 40).getTime(),
                "Realizan venta de libras esterlinas", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 32, 40).getTime(),
                "Hola, en este momento no estamos ofreciendo ese servicio.", false));
        chat
                .addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 32, 45).getTime(), "Chau!",
                        true));
        chat.setSuccessful(false);

        loggingService.persistChat(chat);

        chat = new Chat("marcela78@gmail.com", server);

        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 11, 21, 10, 45).getTime(),
                "Queria consultarles si ofrecen venta de acciones?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 11, 21, 10, 55).getTime(),
                "Ofrecemos venta de acciones de Argentina y EEUU.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 11, 21, 11, 20).getTime(),
                "Deseo comprar acciones de ALUAR", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 11, 21, 11, 30).getTime(),
                "La cotización es de 3.4 pesos por acción. ¿Desea confirmar " + "la compra?.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 11, 21, 11, 35).getTime(), "Si", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 11, 21, 11, 45).getTime(),
                "La compra ha sido confirmada, su código de registración es " + "FFF2222", false));
        chat.setSuccessful(true);
        chat.setFinalNode("comprar acciones");
        loggingService.persistChat(chat);

        chat = new Chat("ferchu@gmail.com", server);

        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 32, 45).getTime(),
                "Ofrecen leasing?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 34, 45).getTime(),
                "Hola, en este momento no estamos ofreciendo ese servicio.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 35, 45).getTime(),
                "Muchas gracias.", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 36, 45).getTime(),
                "De nada, esperamos que vuelva a contactarse.", false));
        chat.setSuccessful(true);
        chat.setFinalNode("leasing");
        loggingService.persistChat(chat);

        chat = new Chat("angie@casablanca.com", server2);

        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 13, 23, 32, 10).getTime(),
                "Hola, shdjfhsdjf", true));
        chat
                .addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 13, 23, 32, 11).getTime(), "Hola.",
                        false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 13, 23, 32, 12).getTime(),
                "sdjkjsdkghsfjk", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 13, 23, 32, 13).getTime(),
                "No entiendo su consulta, la podría repetir?", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 13, 23, 32, 14).getTime(), "bnmnm,cv",
                true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 13, 23, 32, 15).getTime(),
                "No entiendo su consulta, la podría repetir?", false));

        chat.setSuccessful(false);

        loggingService.persistChat(chat);

        chat = new Chat("juli@elserver.org", server2);

        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 32, 45).getTime(),
                "A que hora juega Argentina?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 33, 45).getTime(),
                "Las sucursales de Argentina se encuentran distribuidas en "
                        + "las siguientes provincias: Buenos Aires, Santa Fe y Córdoba.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 14, 34, 45).getTime(),
                "Yo no te pregunte eso te pregunte por el partido de fútbol.", true));
        chat.setSuccessful(false);

        loggingService.persistChat(chat);

        chat = new Chat("elyuyo@campu.org", server2);

        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 9, 9, 32, 45).getTime(),
                "Hola, queria realizar una consulta para obtener una caja de " + "ahorro?", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 9, 9, 34, 45).getTime(),
                "Hola, perfecto en dólares o pesos?", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 9, 9, 35, 45).getTime(), "En pesos.",
                true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 9, 9, 36, 45).getTime(),
                "Para obtener una caja de ahorro requiere un certificado de " + "háber y un domicilio certificado.",
                false));
        chat.setSuccessful(true);
        chat.setFinalNode("caja de ahorro");

        loggingService.persistChat(chat);

        chat = new Chat("elyuyo@campu.org", server);

        chat
                .addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 32, 45).getTime(), "Hola.",
                        true));
        chat
                .addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 33, 05).getTime(), "Hola.",
                        false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 33, 15).getTime(),
                "Me gustaría obtener información acerca de un préstamo.", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 33, 45).getTime(),
                "¿Cuál es el monto que desea obtener?", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 34, 03).getTime(),
                "Deseo obtener 5000.", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 34, 30).getTime(),
                "¿Cuál es su ingreso mensual?", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 34, 45).getTime(), "Gano 2500.",
                true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 35, 45).getTime(),
                "¿Dispone de otro préstamo activo?", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 35, 55).getTime(), "No.", true));
        chat
                .addChatEntry(new ChatEntry(
                        chat,
                        new GregorianCalendar(2008, 11, 10, 19, 35, 57).getTime(),
                        "El banco le ofrece un préstamo de 5000 a pagar en 12 meses, "
                                + " con una tasa anual del 21%. Para obtenerlo debe venir a cualquier sucursal del banco con el DNI y "
                                + "un domicilio certificado.", false));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 36, 05).getTime(),
                "Muchas gracias, chau.", true));
        chat.addChatEntry(new ChatEntry(chat, new GregorianCalendar(2008, 11, 10, 19, 36, 45).getTime(),
                "De nada, fue un gusto poder resolver sus problemas.", false));
        chat.setSuccessful(true);
        chat.setFinalNode("prestamo");

        loggingService.persistChat(chat);
    }

    public void loadActions() {
        ChatterbotService chatterbotService = (ChatterbotService) ctx.getBean("chatterbotService");

        chatterbotService.persistBaseAction(new WebServiceAction("Consultar saldo",
                "http://localhost:8081/chatterbot-business/clients?wsdl", "Clients", "getSaldoCuenta"));

        chatterbotService.persistBaseAction(new WebServiceAction("Validar número de cuenta",
                "http://localhost:8081/chatterbot-business/clients?wsdl", "Clients", "validarNumeroCuenta"));

        chatterbotService.persistBaseAction(new WebServiceAction("Validar número de tarjeta de crédito",
                "http://localhost:8081/chatterbot-business/clients?wsdl", "Clients", "validarNumeroTarjeta"));

        chatterbotService.persistBaseAction(new WebServiceAction("Consultar vencimiento de tarjeta de crédito",
                "http://localhost:8081/chatterbot-business/clients?wsdl", "Clients", "consultarVencimientoTarjeta"));
    }
    
    public void loadErrorLogs() {
        LoggingService loggingService = (LoggingService) ctx.getBean("loggingService");
        
        ErrorLog errorLog;
        
        errorLog = new ErrorLog("UncategorizedSQLException", "org.springframework.jdbc.UncategorizedSQLException: Hibernate operation: Cannot open connection; uncategorized SQLException for SQL [???]; SQL state [null]; error code [17002]; Io exception: The Network Adapter could not establish the connection; nested exception is java.sql.SQLException: Io exception: The Network Adapter could not establish the connection");
        loggingService.persistErrorLog(errorLog);
        
        
        errorLog = new ErrorLog("SQLException", "java.sql.SQLException: Io exception: The Network Adapter could not establish the connection " +
                " at oracle.jdbc.driver.DatabaseError.throwSqlException(DatabaseError.java:125) " + 
                " at oracle.jdbc.driver.DatabaseError.throwSqlException(DatabaseError.java:162) " +
                " at oracle.jdbc.driver.DatabaseError.throwSqlException(DatabaseError.java:274)");
        loggingService.persistErrorLog(errorLog);

        errorLog = new ErrorLog("XMPPException", "Error creating bean with name 'jabberClient' defined in ServletContext resource [/WEB-INF/springContext-beans.xml]: Invocation of init method failed; nested exception is ar.edu.itba.tpf.chatterbot.exception.ChatterbotClientException: No se puede conectar con el servidor Jabber.");
        loggingService.persistErrorLog(errorLog);

    }

    public static void main(String[] args) {
        StartUpDatabase db = new StartUpDatabase();

        db.createSchemaFile("../schema.sql");
        db.loadUsers();
        db.loadServers();
        db.loadActions();
        db.loadNodes();
        db.loadGlobalNodes();
        db.loadErrorLogs();
        db.loadChats();
    }
}