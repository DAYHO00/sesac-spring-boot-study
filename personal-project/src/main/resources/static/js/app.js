
const API_URL = "/api/items";

const registerForm = document.getElementById("registerForm");
const recommendForm = document.getElementById("recommendForm");

// 1. 캠핑 장비 등록
registerForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const nameInput = document.getElementById("itemName");
    const priceInput = document.getElementById("itemPrice");
    const message = document.getElementById("registerMessage");

    const name = nameInput.value.trim();
    const priceText = priceInput.value.trim();
    const price = Number(priceText);

    if (!name) {
        showMessage(message, "장비 이름을 입력해 주세요.", true);
        return;
    }

    if (
        priceText === "" ||
        !Number.isInteger(price) ||
        price < 0 ||
        price > 2147483647
    ) {
        showMessage(message, "가격은 0 이상의 정수로 입력해 주세요.", true);
        return;
    }

    try {
        // @RequestParam에 맞춰 form 데이터 전송
        const params = new URLSearchParams();
        params.append("name", name);
        params.append("price", price);

        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: params.toString()
        });

        if (!response.ok) {
            throw new Error("장비 등록에 실패했습니다.");
        }

        const item = await response.json();

        showMessage(message, item.name + " 장비가 등록되었습니다.", false);

        nameInput.value = "";
        priceInput.value = "";

        await loadItems();
        await loadRecommendations();

    } catch (error) {
        showMessage(message, error.message, true);
    }
});

// 2. 전체 캠핑 장비 조회
async function loadItems() {
    const itemList = document.getElementById("itemList");

    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("장비 목록을 불러오지 못했습니다.");
        }

        const items = await response.json();

        itemList.replaceChildren();

        if (items.length === 0) {
            appendEmpty(itemList, "아직 등록된 캠핑 장비가 없습니다.");
            return;
        }

        items.forEach(function (item) {
            const row = document.createElement("div");
            row.className = "item";

            const left = document.createElement("div");

            const id = document.createElement("span");
            id.className = "item-id";
            id.textContent = "#" + item.id;

            const name = document.createElement("span");
            name.className = "item-name";
            name.textContent = item.name;

            const price = document.createElement("span");
            price.className = "item-price";
            price.textContent = formatPrice(item.price);

            left.append(id, name);
            row.append(left, price);
            itemList.appendChild(row);
        });

    } catch (error) {
        itemList.replaceChildren();
        appendEmpty(itemList, error.message, true);
    }
}

// 3. 예산별 캠핑 장비 추천
recommendForm.addEventListener("submit", async function (event) {
    event.preventDefault();
    await loadRecommendations();
});

async function loadRecommendations() {
    const budgetText = document.getElementById("budget").value.trim();
    const recommendList = document.getElementById("recommendList");

    recommendList.replaceChildren();

    if (budgetText === "") {
        appendEmpty(recommendList, "예산을 입력하면 장비를 추천해 드립니다.");
        return;
    }

    const budget = Number(budgetText);

    if (
        !Number.isInteger(budget) ||
        budget < 0 ||
        budget > 2147483647
    ) {
        appendEmpty(recommendList, "예산은 0 이상의 정수로 입력해 주세요.", true);
        return;
    }

    try {
        const response = await fetch(
            API_URL + "/recommend?budget=" + encodeURIComponent(budget)
        );

        if (!response.ok) {
            throw new Error("장비 추천에 실패했습니다.");
        }

        const recommendations = await response.json();

        if (recommendations.length === 0) {
            appendEmpty(recommendList, "예산에 맞는 캠핑 장비가 없습니다.");
            return;
        }

        recommendations.forEach(function (item, index) {
            const row = document.createElement("div");
            row.className = "recommend-item";

            const name = document.createElement("span");
            name.textContent = (index + 1) + ". " + item.name;

            const price = document.createElement("strong");
            price.textContent = formatPrice(item.price);

            row.append(name, price);
            recommendList.appendChild(row);
        });

    } catch (error) {
        appendEmpty(recommendList, error.message, true);
    }
}

// 가격 형식
function formatPrice(price) {
    return price.toLocaleString("ko-KR") + "원";
}

// 메시지 출력
function showMessage(element, text, isError) {
    element.textContent = text;
    element.className = isError ? "message error" : "message";
}

// 빈 목록 또는 오류 메시지 출력
function appendEmpty(container, text, isError = false) {
    const element = document.createElement("div");
    element.className = isError ? "empty error" : "empty";
    element.textContent = text;
    container.appendChild(element);
}

// 최초 페이지 접속 시 서버에서 목록 조회
loadItems();
