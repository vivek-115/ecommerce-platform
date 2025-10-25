package com.example.ecomDemo.Service;

import com.example.ecomDemo.Entity.Cart;
import com.example.ecomDemo.Entity.Category;
import com.example.ecomDemo.Entity.Product;
import com.example.ecomDemo.Exception.APIException;
import com.example.ecomDemo.Exception.ResourceNotFoundException;
import com.example.ecomDemo.PayLoad.CartDTO;
import com.example.ecomDemo.PayLoad.ProductDTO;
import com.example.ecomDemo.PayLoad.ProductResponse;
import com.example.ecomDemo.Repositry.CartRepo;
import com.example.ecomDemo.Repositry.CategoryRepo;
import com.example.ecomDemo.Repositry.ProductRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Value("${image.base.url}")
    private String imageBaseUrl;


    @Override
    public ProductDTO createProduct(ProductDTO productDTO, Integer categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category with Category Id:"+categoryId+" does not exist"));

        boolean isProductNotPresent=true;
        List<Product> productList = category.getProductList();
        for(Product values:productList){
            if (values.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if(isProductNotPresent){
            Product product=new Product();
            product.setCategory(category);
            product.setProductName(productDTO.getProductName());
            product.setDescription(productDTO.getDescription());
            product.setQuantity(productDTO.getQuantity());
            product.setImage("default.png");
            product.setPrice(productDTO.getPrice());
            product.setDiscount(productDTO.getDiscount());
            double specialPrice=productDTO.getPrice()-((productDTO.getDiscount()*0.01)*productDTO.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepo.save(product);
            return modelMapper.map(savedProduct,ProductDTO.class);
        }else{
            throw new APIException("Product with name: "+productDTO.getProductName()+" already Exits");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,String keyword, String category) {

        Sort sortOrderandBy=sortOrder.equalsIgnoreCase("asc")?
                                    Sort.by(sortBy).ascending()
                                    :Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortOrderandBy);
        Specification<Product> spec=Specification.where(null);
        if(keyword!=null && !keyword.isEmpty()){
            spec=spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")),"%"+keyword.toLowerCase()+"%"));
        }

        if(category!=null && !category.isEmpty()){
            spec=spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("category").get("categoryName"),category));
        }



     Page<Product> productPage=   productRepo.findAll(spec,pageDetails);
     List<Product> all =productPage.getContent();

        if (all.isEmpty()){
            throw new ResourceNotFoundException("No Products found");
        }
      List<ProductDTO> listOfProducts = all.stream()
              .map(product -> {
                  ProductDTO productDTO=modelMapper.map(product, ProductDTO.class);
                productDTO.setImage(constructImageUrl(product.getImage()));
                return productDTO;
              })
              .toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(listOfProducts);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    private String constructImageUrl(String imageName){
        return imageBaseUrl.endsWith("/")?imageBaseUrl+imageName:imageBaseUrl+"/"+imageName;
    }

    @Override
    public ProductResponse getProductsbyCategory(Integer categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
     Category category=   categoryRepo.findById(categoryId)
                                            .orElseThrow(()->new ResourceNotFoundException("Category with Category Id: "+categoryId+" does not exists"));

        Sort sortOrderandBy=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortOrderandBy);
        Page<Product> productPage=  productRepo.findByCategoryOrderByPriceAsc(category,pageDetails);
      List<Product> product=  productPage.getContent();
    if(product.isEmpty()){
        throw new ResourceNotFoundException("No Product in Category with Category Id: "+categoryId+" exists");
    }

   List<ProductDTO>productDTOS= product.stream().map(pro->modelMapper.map(pro,ProductDTO.class)).toList();
    ProductResponse productResponse=new ProductResponse();
    productResponse.setContent(productDTOS);
    productResponse.setPageNumber(productPage.getNumber());
    productResponse.setPageSize(productPage.getSize());
    productResponse.setTotalElements(productPage.getTotalElements());
    productResponse.setTotalPages(productPage.getTotalPages());
    productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }


    //Search Feature
    @Override
    public ProductResponse getProductsByKeywords(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortOrderandBy=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortOrderandBy);

     Page<Product> productPage=   productRepo.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageDetails);
        List<Product> productList = productPage.getContent();

        if (productList.isEmpty()){
         throw new ResourceNotFoundException("No results Found for the Search");
     }
   List<ProductDTO> productDTOS=  productList.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
   ProductResponse productResponse=new ProductResponse();
   productResponse.setContent(productDTOS);
   productResponse.setPageNumber(productPage.getNumber());
   productResponse.setPageSize(productPage.getSize());
   productResponse.setTotalElements(productPage.getTotalElements());
   productResponse.setTotalPages(productPage.getTotalPages());
   productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    //update product service
    @Override
    public ProductDTO updateProductByCategory(ProductDTO productDTO,Integer productId) {
       Product existingProduct= productRepo.findById(productId).
               orElseThrow(()->new ResourceNotFoundException("No Product with Product Id: "+productId+" found"));

       existingProduct.setProductName(productDTO.getProductName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setQuantity(productDTO.getQuantity());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setDiscount(productDTO.getDiscount());
        double specialPrice=productDTO.getPrice()-((productDTO.getDiscount()*0.01)*productDTO.getPrice());
        existingProduct.setSpecialPrice(specialPrice);

     Product product=   productRepo.save(existingProduct);


     //This piece of code is added so that once the product is updated then the product in the
     // user's cart also gets updated
        List<Cart> carts = cartRepo.findCartsByProductId(productId);

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).toList();

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));



        return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProductById(Integer productId) {
      Product product=  productRepo.findById(productId).
                orElseThrow(()->new ResourceNotFoundException("No Product with Product Id: "+productId+" found"));

        productRepo.deleteById(productId);
        return modelMapper.map(product,ProductDTO.class);
    }

    //  //update Product image service
    @Override
    public ProductDTO updateProductImage(Integer productId, MultipartFile image) throws IOException {
        Product product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("No Product with Product Id: "+productId+" exists"));

        String filename=fileService.uploadImage(path,image);

        product.setImage(filename);
       Product savedProduct=  productRepo.save(product);

        return modelMapper.map(savedProduct,ProductDTO.class);
    }


}
